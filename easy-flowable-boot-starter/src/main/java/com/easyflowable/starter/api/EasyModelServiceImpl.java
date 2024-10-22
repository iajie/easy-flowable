package com.easyflowable.starter.api;

import com.easyflowable.core.domain.interfaces.EasyFlowEntityInterface;
import com.easyflowable.core.utils.StringUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.easyflowable.core.domain.entity.EasyModel;
import com.easyflowable.core.domain.entity.EasyModelHistory;
import com.easyflowable.core.exception.EasyFlowableException;
import com.easyflowable.core.mapper.EasyModelHistoryMapper;
import com.easyflowable.core.mapper.EasyModelMapper;
import com.easyflowable.core.service.EasyModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @package: {@link com.easyflowable.starter.api}
 * @Date: 2024-09-27-14:13
 * @Description:
 * @Author: MoJie
 */
@Transactional
public class EasyModelServiceImpl implements EasyModelService {

    @Autowired
    private EasyFlowEntityInterface entityInterface;
    @Autowired
    private EasyModelHistoryMapper modelHistoryMapper;
    @Autowired
    private EasyModelMapper modelMapper;

    @Override
    public boolean insert(EasyModel model) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(EasyModel::getKey, model.getKey());
        // 模型版本
        model.setPublishVersion(0);
        if (modelMapper.selectCountByQuery(queryWrapper) > 0) {
            throw new EasyFlowableException("当前模型标识已存在，无法创建!");
        }
        model.setTenantId(entityInterface.getTenantId());
        model.setOrganId(entityInterface.getOrganId());
        return modelMapper.insert(model) > 0;
    }

    @Override
    public boolean updateById(EasyModel model) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(EasyModel::getKey, model.getKey());
        queryWrapper.ne(EasyModel::getId, model.getId());
        if (modelMapper.selectCountByQuery(queryWrapper) > 0) {
            throw new EasyFlowableException("当前模型标识已存在，无法创建!");
        }
        return modelMapper.update(model) > 0;
    }

    @Override
    public EasyModel getById(String id) {
        return modelMapper.selectOneById(id);
    }

    @Override
    public boolean removeById(String id) {
        return modelMapper.deleteById(id) > 0;
    }

    @Override
    public Page<EasyModel> queryPage(int current, int size, EasyModel model) {
        String tenantId = entityInterface.getTenantId();
        String organId = entityInterface.getOrganId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like(EasyModel::getName, model.getName(), StringUtils.isNotBlank(model.getName()));
        queryWrapper.like(EasyModel::getKey, model.getKey(), StringUtils.isNotBlank(model.getKey()));
        queryWrapper.eq(EasyModel::getModelType, model.getModelType(), model.getModelType() != null);
        queryWrapper.eq(EasyModel::getTenantId, tenantId, tenantId != null);
        queryWrapper.eq(EasyModel::getOrganId, organId, organId != null);
        queryWrapper.orderBy(EasyModel::getCreateTime).desc();
        return modelMapper.paginate(current, size, queryWrapper);
    }

    @Override
    public List<EasyModelHistory> listModelHistory(String modelId) {
        List<EasyModelHistory> list = modelHistoryMapper.selectListByQuery(QueryWrapper.create()
                .orderBy(EasyModelHistory::getCreateTime).desc()
                .eq(EasyModelHistory::getModelId, modelId));
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        return list;
    }

    @Override
    public Page<EasyModelHistory> modelHistory(Page<EasyModelHistory> page, EasyModelHistory modelHistory) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(EasyModelHistory::getModelId, modelHistory.getModelId());
        queryWrapper.orderBy(EasyModelHistory::getCreateTime).desc();
        return modelHistoryMapper.paginate(page, queryWrapper);
    }

    @Override
    public void saveHistory(EasyModelHistory modelHistory) {
        this.modelHistoryMapper.insert(modelHistory);
    }

    @Override
    public boolean deleteHistoryByModelId(String modelId) {
        this.modelHistoryMapper.deleteByQuery(QueryWrapper.create().eq(EasyModelHistory::getModelId, modelId));
        return true;
    }

    @Override
    public boolean modelHistoryRollback(String historyId) {
        EasyModelHistory history = this.modelHistoryMapper.selectOneById(historyId);
        if (history == null) {
            throw new EasyFlowableException("流程历史信息不存在, 回滚失败！");
        }
        return UpdateChain.of(this.modelMapper).eq(EasyModel::getId, history.getModelId())
                .set(EasyModel::getModelEditorXml, history.getModelEditorXml())
                .set(EasyModel::getThumbnail, null)
                .update();
    }
}

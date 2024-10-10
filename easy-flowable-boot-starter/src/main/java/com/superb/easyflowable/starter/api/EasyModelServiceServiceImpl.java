package com.superb.easyflowable.starter.api;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.superb.easyflowable.core.domain.entity.EasyModel;
import com.superb.easyflowable.core.domain.entity.EasyModelHistory;
import com.superb.easyflowable.core.mapper.EasyModelHistoryMapper;
import com.superb.easyflowable.core.mapper.EasyModelMapper;
import com.superb.easyflowable.core.service.EasyModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @package: {@link com.superb.easyflowable.starter.service}
 * @Date: 2024-09-27-14:13
 * @Description:
 * @Author: MoJie
 */
@Service
@Transactional
public class EasyModelServiceServiceImpl extends ServiceImpl<EasyModelMapper, EasyModel> implements EasyModelService {

    @Autowired
    private EasyModelHistoryMapper modelHistoryMapper;

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
}

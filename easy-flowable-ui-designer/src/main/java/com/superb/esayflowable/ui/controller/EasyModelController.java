package com.superb.esayflowable.ui.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.easyflowable.core.domain.entity.EasyModelHistory;
import com.superb.easyflowable.core.utils.StringUtils;
import com.superb.easyflowable.starter.entity.EasyUiModel;
import com.superb.easyflowable.starter.entity.EasyUiModelHistory;
import com.superb.easyflowable.starter.service.EasyModelHistoryService;
import com.superb.easyflowable.starter.service.EasyModelService;
import com.superb.esayflowable.ui.model.PageParams;
import com.superb.esayflowable.ui.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @package: {@link com.superb.esayflowable.ui.controller}
 * @Date: 2024-09-27-12:47
 * @Description: 模型相关接口
 * @Author: MoJie
 */
@RestController
@RequestMapping("easy-flowable/model")
public class EasyModelController {

    @Autowired
    private EasyModelService modelService;
    @Autowired
    private EasyModelHistoryService modelHistoryService;

    /**
     * 模型分页查询
     * @param pageParams 分页查询
     * @return {@link Page<EasyUiModel>}
     * @Author: MoJie
     * @Date: 2024-09-27 14:27:20
     */
    @PostMapping("pageQuery")
    public Result<Page<EasyUiModel>> page(@RequestBody PageParams<EasyUiModel> pageParams) {
        EasyUiModel params = pageParams.getParams();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like(EasyUiModel::getName, params.getName(), StringUtils.isNotBlank(params.getName()));
        queryWrapper.like(EasyUiModel::getKey, params.getKey(), StringUtils.isNotBlank(params.getKey()));
        queryWrapper.orderBy(EasyUiModel::getCreateTime);
        return Result.success(modelService.page(pageParams.getPage(), queryWrapper));
    }

    /**
     * 模型发布历史分页查询
     * @param pageParams 分页查询
     * @return {@link Page<EasyUiModel>}
     * @Author MoJie
     * @Date 2024-09-27 14:27:20
     */
    @PostMapping("historyPage")
    public Result<Page<EasyUiModelHistory>> historyPage(@RequestBody PageParams<EasyUiModelHistory> pageParams) {
        EasyUiModelHistory params = pageParams.getParams();
        QueryWrapper queryWrapper = new QueryWrapper();
        if (StringUtils.isBlank(params.getModelId())) {
            return Result.error("模型ID不能为空");
        }
        queryWrapper.like(EasyUiModelHistory::getModelId, params.getModelId());
        queryWrapper.orderBy(EasyUiModelHistory::getCreateTime);
        return Result.success(modelHistoryService.page(pageParams.getPage(), queryWrapper));
    }

    @PostMapping("save")
    public Result<Boolean> save(@RequestBody EasyUiModel model) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(EasyUiModel::getKey, model.getKey());
        if (StringUtils.isNotBlank(model.getId())) {
            model.setUpdateTime(new Date());
            queryWrapper.ne(EasyUiModel::getId, model.getId());
        } else {
            // 模型版本
            model.setPublishVersion(0);
        }
        if (modelService.count(queryWrapper) > 0) {
            return Result.error("当前模型标识已存在，无法创建!");
        }
        return Result.success(modelService.saveOrUpdate(model));
    }

    @GetMapping("remove/{id}")
    public Result<Boolean> remove(@PathVariable String id) {
        // 删除流程模型发布历史
        if (modelHistoryService.remove(QueryWrapper.create().eq(EasyModelHistory::getModelId, id))) {
            return Result.success(modelService.removeById(id));
        }
        return Result.error();
    }

    @GetMapping("info/{id}")
    public Result<EasyUiModel> getInfo(@PathVariable String id) {
        return Result.success(modelService.getById(id));
    }
}

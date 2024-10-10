package com.superb.easyflowable.ui.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.easyflowable.core.utils.StringUtils;
import com.superb.easyflowable.core.domain.entity.EasyModel;
import com.superb.easyflowable.core.domain.entity.EasyModelHistory;
import com.superb.easyflowable.core.service.EasyModelService;
import com.superb.easyflowable.ui.model.PageParams;
import com.superb.easyflowable.ui.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @package: {@link com.superb.easyflowable.ui.controller}
 * @Date: 2024-09-27-12:47
 * @Description: 模型相关接口
 * @Author: MoJie
 */
@RestController
@RequestMapping("easy-flowable/model")
public class EasyModelController {

    @Autowired
    private EasyModelService modelService;

    /**
     * 模型分页查询
     * @param pageParams 分页查询
     * @return {@link Page< EasyModel >}
     * @Author: MoJie
     * @Date: 2024-09-27 14:27:20
     */
    @PostMapping("pageQuery")
    public Result<Page<EasyModel>> page(@RequestBody PageParams<EasyModel> pageParams) {
        EasyModel params = pageParams.getParams();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like(EasyModel::getName, params.getName(), StringUtils.isNotBlank(params.getName()));
        queryWrapper.like(EasyModel::getKey, params.getKey(), StringUtils.isNotBlank(params.getKey()));
        queryWrapper.eq(EasyModel::getModelType, params.getModelType(), params.getModelType() != null);
        queryWrapper.orderBy(EasyModel::getCreateTime);
        return Result.success(modelService.page(pageParams.getPage(), queryWrapper));
    }

    /**
     * 模型发布历史分页查询
     * @param pageParams 分页查询
     * @return {@link Page< EasyModel >}
     * @Author MoJie
     * @Date 2024-09-27 14:27:20
     */
    @PostMapping("historyPage")
    public Result<Page<EasyModelHistory>> historyPage(@RequestBody PageParams<EasyModelHistory> pageParams) {
        EasyModelHistory params = pageParams.getParams();
        if (StringUtils.isBlank(params.getModelId())) {
            return Result.error("模型ID不能为空");
        }
        return Result.success(modelService.modelHistory(pageParams.getPage(), params));
    }

    @PostMapping("save")
    public Result<Boolean> save(@RequestBody EasyModel model) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(EasyModel::getKey, model.getKey());
        if (StringUtils.isNotBlank(model.getId())) {
            model.setUpdateTime(new Date());
            queryWrapper.ne(EasyModel::getId, model.getId());
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
        modelService.deleteHistoryByModelId(id);
        if (modelService.removeById(id)) {
            return Result.success();
        }
        return Result.error();
    }

    @GetMapping("info/{id}")
    public Result<EasyModel> getInfo(@PathVariable String id) {
        return Result.success(modelService.getById(id));
    }
}

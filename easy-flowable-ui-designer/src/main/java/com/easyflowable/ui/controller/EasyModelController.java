package com.easyflowable.ui.controller;

import com.mybatisflex.core.paginate.Page;
import com.easyflowable.core.utils.StringUtils;
import com.easyflowable.core.domain.entity.EasyModel;
import com.easyflowable.core.domain.entity.EasyModelHistory;
import com.easyflowable.core.service.EasyModelService;
import com.easyflowable.ui.model.PageParams;
import com.easyflowable.ui.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @package: {@link com.easyflowable.ui.controller}
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
     * @param pageParams 分页查询
     * @return {@link Result} {@link Page} {@link EasyModel} 
     * @Author: MoJie
     * @Date: 2024-10-12 11:09
     * @Description: 模型分页查询
     */
    @PostMapping("pageQuery")
    public Result<Page<EasyModel>> page(@RequestBody PageParams<EasyModel> pageParams) {
        EasyModel params = pageParams.getParams();
        return Result.success(modelService.queryPage(pageParams.getCurrent(), pageParams.getSize(), params));
    }

    /**
     * 模型发布历史分页查询
     * @param pageParams 分页查询
     * @return {@link Page<EasyModel>}
     * @Author: MoJie
     * @Date: 2024-09-27 14:27:20
     */
    @PostMapping("historyPage")
    public Result<Page<EasyModelHistory>> historyPage(@RequestBody PageParams<EasyModelHistory> pageParams) {
        EasyModelHistory params = pageParams.getParams();
        if (StringUtils.isBlank(params.getModelId())) {
            return Result.error("模型ID不能为空");
        }
        return Result.success(modelService.modelHistory(pageParams.getPage(), params));
    }

    /**
     * @param historyId 历史ID
     * @return {@link Result} {@link Boolean}
     * @Author: MoJie
     * @Date: 2024-10-12 11:21
     * @Description: 版本回滚 不能回滚缩略图
     */
    @GetMapping("rollback/{historyId}")
    public Result<Boolean> rollback(@PathVariable String historyId) {
        return Result.success(modelService.modelHistoryRollback(historyId));
    }

    /**
     * @param model 模型参数
     * @return {@link Result<Boolean>}
     * @Author: MoJie
     * @Date: 2024-10-12 10:37
     * @Description: 保存模型
     */
    @PostMapping("save")
    public Result<Boolean> save(@RequestBody EasyModel model) {
        if (StringUtils.isNotBlank(model.getId())) {
            return Result.success(modelService.updateById(model));
        }
        return Result.success(modelService.insert(model));
    }

    /**
     * @param id 模型ID
     * @return {@link Result<Boolean>}
     * @Author: MoJie
     * @Date: 2024-10-12 10:41
     * @Description: 根据ID删除流程模型
     */
    @GetMapping("remove/{id}")
    public Result<Boolean> remove(@PathVariable String id) {
        // 删除流程模型发布历史
        modelService.deleteHistoryByModelId(id);
        if (modelService.removeById(id)) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * @param id 模型ID
     * @return {@link Result<EasyModel>}
     * @Author: MoJie
     * @Date: 2024-10-12 10:42
     * @Description: 根据ID获取模型信息
     */
    @GetMapping("info/{id}")
    public Result<EasyModel> getInfo(@PathVariable String id) {
        return Result.success(modelService.getById(id));
    }
}

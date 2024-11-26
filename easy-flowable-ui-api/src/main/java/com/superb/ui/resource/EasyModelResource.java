package com.superb.ui.resource;

import com.superb.core.constans.Constants;
import com.superb.core.domain.dto.Page;
import com.superb.core.domain.entity.EasyModel;
import com.superb.core.domain.model.PageParams;
import com.superb.core.domain.model.Result;
import com.superb.core.service.EasyModelService;
import com.superb.core.utils.EasyFlowableStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 模型相关接口
 * @since 1.0  2024-09-27-12:47
 * @author MoJie
 */
@RestController
@RequestMapping(Constants.EASY_FLOWABLE + "/model")
public class EasyModelResource {

    @Autowired(required = false)
    private EasyModelService modelService;

    /**
     * 模型分页查询
     * @param pageParams 分页查询
     * @return {@link Result} {@link Page} {@link EasyModel}
     * @author MoJie
     * @since 1.0  2024-10-12 11:09
     */
    @PostMapping("/pageQuery")
    public Result<Page<EasyModel>> page(@RequestBody PageParams<EasyModel> pageParams) {
        EasyModel params = pageParams.getParams();
        return Result.success(modelService.queryPage(pageParams.getCurrent(), pageParams.getPageSize(), params));
    }

    /**
     * 保存模型
     * @param model 模型参数
     * @return {@link Result<Boolean>}
     * @author MoJie
     * @since 1.0  2024-10-12 10:37
     */
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody EasyModel model) {
        if (EasyFlowableStringUtils.isNotBlank(model.getId())) {
            return Result.success(modelService.updateById(model));
        }
        return Result.success(modelService.insert(model));
    }

    /**
     * 根据ID删除流程模型
     * @param id 模型ID
     * @return {@link Result<Boolean>}
     * @author MoJie
     * @since 1.0  2024-10-12 10:41
     */
    @GetMapping("/remove/{id}")
    public Result<Boolean> remove(@PathVariable String id) {
        if (modelService.removeById(id)) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 根据ID获取模型信息
     * @param id 模型ID
     * @return {@link Result<EasyModel>}
     * @author MoJie
     * @since 1.0  2024-10-12 10:42
     */
    @GetMapping("/info/{id}")
    public Result<EasyModel> getInfo(@PathVariable String id) {
        return Result.success(modelService.getById(id));
    }
}

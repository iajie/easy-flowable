package com.superb.solon.resource;

import com.superb.core.constans.Constants;
import com.superb.core.domain.dto.Page;
import com.superb.core.domain.entity.EasyModel;
import com.superb.core.domain.model.PageParams;
import com.superb.core.domain.model.Result;
import com.superb.core.service.EasyModelService;
import com.superb.core.utils.EasyFlowableStringUtils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;

/**
 * 模型相关接口
 * @since 1.0  2024-09-27-12:47
 * @author MoJie
 */
@Controller
@Mapping(Constants.EASY_FLOWABLE + "/model")
public class EasyModelResource {

    @Inject
    private EasyModelService modelService;

    /**
     * @param pageParams 分页查询
     * @return {@link Result} {@link Page} {@link EasyModel} 
     * @author MoJie
     * @since 1.0  2024-10-12 11:09
     *  模型分页查询
     */
    @Mapping(value = "/pageQuery", method = MethodType.POST)
    public Result<Page<EasyModel>> page(@Body PageParams<EasyModel> pageParams) {
        EasyModel params = pageParams.getParams();
        return Result.success(modelService.queryPage(pageParams.getCurrent(), pageParams.getPageSize(), params));
    }

    /**
     * @param model 模型参数
     * @return {@link Result<Boolean>}
     * @author MoJie
     * @since 1.0  2024-10-12 10:37
     *  保存模型
     */
    @Mapping(value = "/save", method = MethodType.POST)
    public Result<Boolean> save(@Body EasyModel model) {
        if (EasyFlowableStringUtils.isNotBlank(model.getId())) {
            return Result.success(modelService.updateById(model));
        }
        return Result.success(modelService.insert(model));
    }

    /**
     * @param id 模型ID
     * @return {@link Result<Boolean>}
     * @author MoJie
     * @since 1.0  2024-10-12 10:41
     *  根据ID删除流程模型
     */
    @Mapping(value = "/remove/{id}", method = MethodType.GET)
    public Result<Boolean> remove(@Param String id) {
        if (modelService.removeById(id)) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * @param id 模型ID
     * @return {@link Result<EasyModel>}
     * @author MoJie
     * @since 1.0  2024-10-12 10:42
     *  根据ID获取模型信息
     */
    @Mapping(value = "/info/{id}", method = MethodType.GET)
    public Result<EasyModel> getInfo(@Param String id) {
        return Result.success(modelService.getById(id));
    }
}

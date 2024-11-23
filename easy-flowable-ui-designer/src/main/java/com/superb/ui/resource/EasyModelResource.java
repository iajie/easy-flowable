package com.superb.ui.resource;

import com.superb.core.constans.Constants;
import com.superb.core.domain.dto.Page;
import com.superb.core.domain.entity.EasyModel;
import com.superb.core.service.EasyModelService;
import com.superb.core.utils.StringUtils;
import com.superb.ui.model.PageParams;
import com.superb.ui.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @package: {@link com.superb.ui.resource}
 * @Date: 2024-09-27-12:47
 * @Description: 模型相关接口
 * @Author: MoJie
 */
@RestController
public class EasyModelResource {

    @Autowired(required = false)
    private EasyModelService modelService;

    /**
     * @param pageParams 分页查询
     * @return {@link Result} {@link Page} {@link EasyModel} 
     * @Author: MoJie
     * @Date: 2024-10-12 11:09
     * @Description: 模型分页查询
     */
    @PostMapping(Constants.EASY_FLOWABLE + "/model/pageQuery")
    public Result<Page<EasyModel>> page(@RequestBody PageParams<EasyModel> pageParams) {
        EasyModel params = pageParams.getParams();
        return Result.success(modelService.queryPage(pageParams.getCurrent(), pageParams.getPageSize(), params));
    }

    /**
     * @param model 模型参数
     * @return {@link Result<Boolean>}
     * @Author: MoJie
     * @Date: 2024-10-12 10:37
     * @Description: 保存模型
     */
    @PostMapping(Constants.EASY_FLOWABLE + "/model/save")
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
    @GetMapping(Constants.EASY_FLOWABLE + "/model/remove/{id}")
    public Result<Boolean> remove(@PathVariable String id) {
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
    @GetMapping(Constants.EASY_FLOWABLE + "/model/info/{id}")
    public Result<EasyModel> getInfo(@PathVariable String id) {
        return Result.success(modelService.getById(id));
    }
}

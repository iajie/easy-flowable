package com.superb.starter.api;

import com.superb.core.domain.dto.Page;
import com.superb.core.domain.entity.EasyModel;
import com.superb.core.exception.EasyFlowableException;
import com.superb.core.service.EasyModelService;
import com.superb.core.utils.EasyUtils;
import com.superb.core.utils.EasyFlowableStringUtils;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.persistence.entity.ModelEntityImpl;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 模型操作方法
 *
 * @author MoJie
 * @since 1.0  2024-09-27-14:13
 */
public class EasyModelServiceImpl implements EasyModelService {

    @Resource
    private RepositoryService repositoryService;

    @Override
    public String insert(EasyModel easyModel) {
        Model result = repositoryService.createModelQuery().modelKey(easyModel.getKey()).singleResult();
        if (result != null) {
            throw new EasyFlowableException("当前模型标识已存在，无法创建!");
        }
        Model model = repositoryService.newModel();
        model.setKey(easyModel.getKey());
        model.setName(easyModel.getName());
        model.setVersion(0);
        model.setCategory(easyModel.getModelType());
        model.setMetaInfo(easyModel.getRemarks());
        repositoryService.saveModel(model);
        return model.getId();
    }

    @Override
    public boolean updateById(EasyModel model) {
        Model result = repositoryService.createModelQuery().modelId(model.getId()).singleResult();
        if (result == null) {
            throw new EasyFlowableException("当前模型不存在，无法操作!");
        }
        if (EasyFlowableStringUtils.isNotBlank(model.getModelType())) {
            result.setCategory(model.getModelType());
        }
        if (EasyFlowableStringUtils.isNotBlank(model.getName())) {
            result.setName(model.getName());
        }
        if (EasyFlowableStringUtils.isNotBlank(model.getRemarks())) {
            result.setMetaInfo(model.getRemarks());
        }
        repositoryService.saveModel(result);
        if (EasyFlowableStringUtils.isNotBlank(model.getModelEditorXml())) {
            // 校验流程信息
            EasyUtils.validateModel(model.getModelEditorXml());
            repositoryService.addModelEditorSource(model.getId(), model.getModelEditorXml()
                    .getBytes(StandardCharsets.UTF_8));
        }
        if (EasyFlowableStringUtils.isNotBlank(model.getPicture())) {
            repositoryService.addModelEditorSourceExtra(model.getId(), model.getPicture()
                    .getBytes(StandardCharsets.UTF_8));
        }
        return true;
    }

    @Override
    public EasyModel getById(String id, boolean existsError) {
        Model model = repositoryService.createModelQuery().modelId(id).latestVersion().singleResult();
        if (model == null) {
            if (existsError) {
                throw new EasyFlowableException("模型不存在！");
            }
            return null;
        }
        return this.getModelAndSource(model);
    }

    private EasyModel getModelAndSource(Model model) {
        EasyModel easyModel = EasyUtils.getModel(model);
        if (model.hasEditorSource()) {
            byte[] modelEditorSource = repositoryService.getModelEditorSource(model.getId());
            easyModel.setModelEditorXml(new String(modelEditorSource, StandardCharsets.UTF_8));
        }
        if (model.hasEditorSourceExtra()) {
            byte[] modelEditorSourceExtra = repositoryService.getModelEditorSourceExtra(model.getId());
            easyModel.setPicture(new String(modelEditorSourceExtra, StandardCharsets.UTF_8));
        }
        return easyModel;
    }

    @Override
    public EasyModel getByKey(String key) {
        Model model = this.repositoryService.createModelQuery().modelKey(key).singleResult();
        if (model != null) {
            return this.getModelAndSource(model);
        }
        return null;
    }

    @Override
    public boolean removeById(String id) {
        repositoryService.deleteModel(id);
        return true;
    }

    @Override
    public Page<EasyModel> queryPage(int current, int size, EasyModel easyModel) {
        ModelQuery modelQuery = repositoryService.createModelQuery().orderByLastUpdateTime().desc();
        if (EasyFlowableStringUtils.isNotBlank(easyModel.getModelType())) {
            modelQuery.modelCategory(easyModel.getModelType());
        }
        if (EasyFlowableStringUtils.isNotBlank(easyModel.getKey())) {
            modelQuery.modelKey(easyModel.getKey());
        }
        if (EasyFlowableStringUtils.isNotBlank(easyModel.getName())) {
            modelQuery.modelNameLike(easyModel.getName());
        }
        Page<EasyModel> page = new Page<>();
        page.setTotal(modelQuery.count());
        List<EasyModel> list = new ArrayList<>();
        List<Model> models = modelQuery.listPage((current - 1) * size, size);
        for (Model model : models) {
            EasyModel modelDo = new EasyModel()
                    .setId(model.getId())
                    .setName(model.getName())
                    .setKey(model.getKey())
                    .setModelType(model.getCategory())
                    .setCreateTime(model.getCreateTime())
                    .setUpdateTime(model.getLastUpdateTime())
                    .setRemarks(model.getMetaInfo());
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(model.getKey())
                    .latestVersion().singleResult();
            if (processDefinition != null) {
                modelDo.setPublishVersion(processDefinition.getVersion());
            }
            if (model.hasEditorSourceExtra()) {
                byte[] modelEditorSourceExtra = repositoryService.
                        getModelEditorSourceExtra(model.getId());
                if (modelEditorSourceExtra != null) {
                    modelDo.setPicture(new String(modelEditorSourceExtra, StandardCharsets.UTF_8));
                }
            }
            list.add(modelDo);
        }
        page.setRecords(list);
        return page;
    }
}

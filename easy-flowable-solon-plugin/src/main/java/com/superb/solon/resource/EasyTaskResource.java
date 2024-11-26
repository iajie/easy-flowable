package com.superb.solon.resource;

import com.superb.core.constans.Constants;
import com.superb.core.domain.dto.FlowUserTask;
import com.superb.core.domain.model.Result;
import com.superb.core.domain.params.FlowCancellationParam;
import com.superb.core.domain.params.FlowExecuteParam;
import com.superb.core.service.EasyTaskService;
import lombok.SneakyThrows;
import org.flowable.engine.TaskService;
import org.flowable.engine.task.Attachment;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.UploadedFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 任务相关api
 * @since 1.0  2024-10-09-17:03
 * @author MoJie
 */
@Controller
@Mapping(Constants.EASY_FLOWABLE + "/task")
public class EasyTaskResource {

    @Inject
    private EasyTaskService easyTaskService;
    @Inject
    private TaskService taskService;

    /**
     * 执行任务
     * @param param {@link FlowExecuteParam} 执行参数
     * @return {@link Result}
     * @author MoJie
     */
    @Mapping(value = "/execute", method = MethodType.POST)
    public Result<?> execute(@Body FlowExecuteParam param) {
        easyTaskService.executeNextStep(param);
        return Result.success();
    }

    /**
     * @param file 文件
     * @param taskId 任务ID
     * @param processInstanceId 流程实例ID
     * @return {@link Result}
     * @author MoJie
     * @since 1.0  2024/10/29 19:28
     *  上传附件,返回附件ID
     */
    @SneakyThrows
    @Mapping(value = "/addAttachment", method = MethodType.POST)
    public Result<String> addAttachment(@Body UploadedFile file, String taskId, String processInstanceId) {
        Attachment attachment = taskService.createAttachment(Constants.FILE, taskId, processInstanceId,
                file.getName(), null, file.getContent());
        return Result.success("附件上传成功！", attachment.getId());
    }

    /**
     * @param attachmentId 文件ID
     * @return {@link Result}
     * @author MoJie
     * @since 1.0  2024/10/29 19:28
     *  删除附件
     */
    @Mapping(value = "/delAttachment/{attachmentId}", method = MethodType.GET)
    public Result<String> delAttachment(@Param String attachmentId) {
        this.taskService.deleteAttachment(attachmentId);
        return Result.success();
    }

    /**
     * @param attachmentId 附件ID
     * @param ctx 上下文对象
     * @author MoJie
     * @since 1.0  2024/10/29 22:11
     *  获取附件
     */
    @SneakyThrows
    @Mapping(value = "/getAttachment/{attachmentId}", method = MethodType.GET)
    public void getAttachment(@Param String attachmentId, Context ctx) {
        try (OutputStream out = ctx.outputStream();
             InputStream in = taskService.getAttachmentContent(attachmentId)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 流程作废
     * @param cancellation 作废流程
     * @return {@link Result}
     * @author MoJie
     * @since 1.0  2024-10-09 17:06:15
     */
    @Mapping(value = "/cancellation", method = MethodType.POST)
    public Result<?> cancellation(@Body FlowCancellationParam cancellation) {
        easyTaskService.cancellationProcessInstance(cancellation);
        return Result.success();
    }

    /**
     * 获取当前任务节点执行人(候选人)
     * @param taskId 任务ID
     * @return {@link Result} {@link List} {@link String}
     * @author MoJie
     */
    @Mapping(value = "/executors/{taskId}", method = MethodType.GET)
    public Result<List<String>> getUserTaskExecutors(@Param String taskId) {
        return Result.success(easyTaskService.getUserTaskExecutors(taskId, false));
    }

    /**
     * 获取当前任务节点执行部门(候选组)
     * @param taskId 任务ID
     * @return {@link Result} {@link List} {@link String}
     * @author MoJie
     */
    @Mapping(value = "/executeOrgan/{taskId}", method = MethodType.GET)
    public Result<List<String>> executeOrgan(@Param String taskId) {
        return Result.success(easyTaskService.getUserTaskOrganIds(taskId));
    }

    /**
     * 获取下一节点信息、流程局部变量、操作方式
     * @param taskId 任务ID
     * @return {@link Result} {@link Map} {@link Object}
     * @author MoJie
     */
    @Mapping(value = "/nextNodeVariables/{taskId}", method = MethodType.GET)
    public Result<Map<String, Object>> nextNodeVariables(@Param String taskId) {
        return Result.success(easyTaskService.nextNodeVariables(taskId));
    }
}

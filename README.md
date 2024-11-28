<p align="center">
    <img src="https://gitee.com/iajie/easy-flowable-ui-admin/raw/master/src/assets/logo-full.png" alt=""/>
</p>



# easy-flowable： 一个出色的 flowable 增强框架

## 特征

#### 1. 很轻量

> easy-flowable 整个框架只依赖 flowable-engine，再无其他任何第三方依赖，摒弃了自带的flowable-ui，使用easy-flowable自行开发的ui控制台。

#### 2. 只增强

> easy-flowable 增加了通用的api，模型操作、模型部署、流程实例、流程实例任务、表单信息比对(非表单流程)，保留flowable原有的功能。

#### 3. 更灵活

> easy-flowable 在如今前后端分离，一般业务系统都有自己的用户体系以及部门等组信息，如果你是新手，那么想要使用自己的数据使用成了难题， easy-flowable
> 帮助你很快实现这件事。

## 交流群群

QQ 群

- 2165024529



## 开始

- [快速开始](https://easy-flowable.online/quick-start.html)
- 示例 1：[easy-flowable + Spring boot](https://gitee.com/iajie/easy-flowable-test/spring-boot)
- 示例 2：[easy-flowable + solon](https://gitee.com/iajie/easy-flowable-test/solon)


```xml
<dependency>
    <groupId>com.easy-flowable</groupId>
    <artifactId>easy-flowable-boot-starter</artifactId>
    <version>${last.version}</version>
</dependency>
```

**启动访问：** http://localhost:port/easy-flowable/index.html

## 模型操作

>  对应数据表ACT_RE_MODEL

### 1. 创建一个模型

POST /easy-flowable/model/save

```json
{
  "key": "qjsq",
  "name": "请假申请",
  "modelType": "01",
  "remarks": "这里是备注..."
}
```

### 2. 流程模型绘制

POST /easy-flowable/model/save

```json
{
  "id": "modelId",
  "name": "请假申请",
  "modelType": "01",
  "remarks": "这里是备注...",
  "modelEditorXml": "bpmn2.0Xml",
  "picture": "流程图(部署后可通过流程模型获取)"
}
```

### 3. 流程部署

> 流程部署后会在ACT_RE_DEPLOYMENT(流程部署)、ACT_RE_PROCDEF(流程定义)两张表里产生数据

GET /easy-flowable/deployment/{modelId}

### 4. 流程启动

POST /easy-flowable/processInstance/start

```json
{
  "flowKey": "流程标识(会使用最新的流程定义启动)",
  "businessKey": "业务主键",
  "skipFirstNode": true,
  "processName": "流程名称",
  "variables": {}
}
```

## 如何使用自己系统中的用户体系
```java
import com.superb.core.service.EasyUserService;

@Component
public class EasyUserServiceImpl implements EasyUserService {

    private final EasyFlowableUiConfig properties;

    @Override
    public EasyFlowableUser getCurrentUser(Object userId) {
        EasyFlowableUser user = new EasyFlowableUser();
        // 根据当前用户获取信息，如果您没有使用ui登陆那么userId就可以不用管，返回自己系统中的用户即可
        return user;
    }

    @Override
    public Object login(String username, String password) {
        // 如果使用ui控制条会使用到，需要开启登录功能
        return userId;
    }

    @Override
    public List<Option> users() {
        List<Option> list = new ArrayList<>();
        // 添加自己的用户列表
        return list;
    }

    @Override
    public List<Option> groups() {
        List<Option> list = new ArrayList<>();
        // 添加自己的组
        return list;
    }
}
```


## 更多文档

- [https://easy-flowable.online](https://easy-flowable.online)
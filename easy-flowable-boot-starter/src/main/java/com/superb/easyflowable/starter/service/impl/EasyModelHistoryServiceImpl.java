package com.superb.easyflowable.starter.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.superb.easyflowable.starter.entity.EasyUiModelHistory;
import com.superb.easyflowable.starter.mapper.EasyModelHistoryMapper;
import com.superb.easyflowable.starter.service.EasyModelHistoryService;
import org.springframework.stereotype.Service;

/**
 * @package: {@link com.superb.easyflowable.starter.service.impl}
 * @Date: 2024-09-27-14:35
 * @Description:
 * @Author: MoJie
 */
@Service
public class EasyModelHistoryServiceImpl extends ServiceImpl<EasyModelHistoryMapper, EasyUiModelHistory> implements EasyModelHistoryService {
}

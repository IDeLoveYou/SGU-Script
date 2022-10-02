package cn.rabig.tools.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author MoNo
 * @since 2022/8/29 15:35
 **/
@Data
@Builder
public class ShellModel {
    //命令执行是否成功
    private boolean isSuccess;
    //成功信息
    private String outMsg;
    //错误信息
    private String errMsg;
}

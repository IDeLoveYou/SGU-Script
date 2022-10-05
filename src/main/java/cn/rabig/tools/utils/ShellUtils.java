package cn.rabig.tools.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.TableMap;
import cn.rabig.tools.model.ShellModel;
import lombok.Data;
import lombok.NonNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author MoNo
 * @since 2022/8/29 0:18
 **/
@Data
public class ShellUtils {
    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private boolean isSuccess = false;
    private Connection connection;

    public ShellUtils(@NonNull String host, @NonNull String port, @NonNull String username, @NonNull String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        start();
    }

    /**
     * 启动连接
     */
    private void start() {
        try {
            Connection conn = new Connection(host, Integer.parseInt(port));
            conn.connect();
            isSuccess = conn.authenticateWithPassword(username, password);
            if (isSuccess) {
                connection = conn;
            } else {
                close();
            }
        } catch (Exception ignored) {
            close();
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    /**
     * 执行远程命令
     *
     * @param command 执行的命令
     * @throws Exception
     */
    public ShellModel exec(@NonNull String command) {
        Session session = null;
        try {
            session = connection.openSession();
            session.execCommand(command);
            String Stdout = IoUtil.readUtf8(session.getStdout());
            String Stderr = IoUtil.readUtf8(session.getStderr());
            if (Stderr.isEmpty()) {
                return ShellModel.builder().isSuccess(true).outMsg(Stdout).build();
            } else {
                return ShellModel.builder().isSuccess(false).errMsg(Stderr).build();
            }
        } catch (Exception e) {
            return ShellModel.builder().isSuccess(false).errMsg("exec函数出现错误").build();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * 输入流上传
     *
     * @param localFile             输入流
     * @param remoteFileName        远程文件名称
     * @param remoteTargetDirectory 上传的目录
     */
    public boolean scpPutFile(byte @NonNull [] localFile,
                              @NonNull String remoteFileName,
                              @NonNull String remoteTargetDirectory,
                              @NonNull String permissions) {
        try {
            exec("mkdir -p " + remoteTargetDirectory);
            SCPClient scpClient = connection.createSCPClient();
            scpClient.put(localFile, remoteFileName, remoteTargetDirectory, permissions);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获得批量执行命令构造器
     *
     * @return cn.rabig.tools.utils.ShellUtils.execCommandList
     * @author MoNo
     * @since 2022/9/14 0:01
     */
    public execCommandList execCommandList() {
        return new execCommandList();
    }


    /**
     * 批量执行命令
     *
     * @author MoNo
     * @return
     * @since 2022/9/13 23:43
     */
    public class execCommandList {
        private final TableMap<String, String> CommandList = new TableMap<>();

        public execCommandList add(String command) {
            CommandList.put(command, "");
            return this;
        }

        public execCommandList add(String command,String errorMessage) {
            CommandList.put(command, errorMessage);
            return this;
        }

        /**
         * 批量执行命令，但中间出现错误并不处理，继续执行
         *
         * @author MoNo
         * @since 2022/9/14 0:04
         */
        public void startWithoutCheck() {
            CommandList.forEach((key, value) -> exec(key));
        }

        /**
         * 批量执行命令，但中间出现错误会重试五次，继续执行
         * @return java.util.AbstractMap.SimpleEntry<java.lang.Boolean,java.lang.String>
         * @since 2022/10/5 13:36
         * @author MoNo
         */
        public SimpleEntry<Boolean, String> startWithCheck() {
            AtomicReference<String> error = new AtomicReference<>("");
            CommandList.forEach((key, value) -> {
                for (int count = 0;count < 5;count++){
                    if (exec(key).isSuccess()) {
                        break;
                    }else if (count == 4){
                        error.set(error.get() + value + "\n");
                    }
                }
            });
            return error.get().isBlank() ? new SimpleEntry<>(true,"全部执行成功") : new SimpleEntry<>(false,error.get());
        }
    }

}

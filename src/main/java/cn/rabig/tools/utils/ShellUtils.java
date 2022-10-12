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
import java.util.stream.IntStream;

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

    /**
     * 创建SSH连接
     *
     * @param host     [java.lang.String]
     * @param port     [java.lang.String]
     * @param username [java.lang.String]
     * @param password [java.lang.String]
     * @return
     * @author MoNo
     * @since 2022/10/12 21:53
     */
    public ShellUtils(@NonNull String host, @NonNull String port, @NonNull String username, @NonNull String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        start();
    }

    /**
     * 启动连接
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:53
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
     *
     * @return void
     * @author MoNo
     * @since 2022/10/12 21:53
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
     * @param command [java.lang.String]
     * @return cn.rabig.tools.model.ShellModel
     * @author MoNo
     * @since 2022/10/12 21:53
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
     * @param localFile             [byte]
     * @param remoteFileName        [java.lang.String]
     * @param remoteTargetDirectory [java.lang.String]
     * @param permissions           [java.lang.String]
     * @return boolean
     * @author MoNo
     * @since 2022/10/12 21:53
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
     * @since 2022/10/12 21:54
     */
    public execCommandList execCommandList() {
        return new execCommandList();
    }

    /**
     * 批量执行命令子类
     *
     * @author MoNo
     * @since 2022/10/12 21:54
     */
    public class execCommandList {
        /**
         * 初始化执行命令链
         */
        private final TableMap<String, String> CommandList = new TableMap<>();

        /**
         * 添加执行命令链
         *
         * @param command [java.lang.String]
         * @return cn.rabig.tools.utils.ShellUtils.execCommandList
         * @author MoNo
         * @since 2022/10/12 21:54
         */
        public execCommandList add(String command) {
            CommandList.put(command, "");
            return this;
        }

        /**
         * 添加执行命令链
         *
         * @param command      [java.lang.String]
         * @param errorMessage [java.lang.String]
         * @return cn.rabig.tools.utils.ShellUtils.execCommandList
         * @author MoNo
         * @since 2022/10/12 21:55
         */
        public execCommandList add(String command, String errorMessage) {
            CommandList.put(command, errorMessage);
            return this;
        }

        /**
         * 批量执行命令，但中间出现错误并不处理，继续执行
         *
         * @return void
         * @author MoNo
         * @since 2022/10/12 21:55
         */
        public void startWithoutCheck() {
            CommandList.forEach((key, value) -> exec(key));
        }

        /**
         * 批量执行命令，但中间出现错误会重试，继续执行
         *
         * @param errorCount [int]
         * @return java.util.AbstractMap.SimpleEntry<java.lang.Boolean, java.lang.String>
         * @author MoNo
         * @since 2022/10/12 21:55
         */
        public SimpleEntry<Boolean, String> startWithCheck(int errorCount) {
            AtomicReference<String> error = new AtomicReference<>("");
            CommandList.forEach((key, value) -> {
                if (IntStream.rangeClosed(1, errorCount).noneMatch(i -> exec(key).isSuccess())) {
                    error.set(error.get() + value + "\n");
                }
            });
            return error.get().isBlank() ? new SimpleEntry<>(true, "全部执行成功") : new SimpleEntry<>(false, error.get());
        }
    }
}

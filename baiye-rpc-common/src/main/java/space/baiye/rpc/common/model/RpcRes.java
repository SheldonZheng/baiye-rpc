package space.baiye.rpc.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Baiye on 2021/12/24.
 *
 * Server 向 Client 返回数据的实体
 *
 * @author Baiye
 */
@Data
public class RpcRes<T> implements Serializable {

    private String requestId;

    private Integer code;

    private String errorMsg;

    private T result;
}

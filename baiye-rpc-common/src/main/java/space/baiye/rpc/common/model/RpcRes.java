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

    private final static Integer SUCCESS_CODE = 0;
    private final static Integer FAIL_CODE = 1;


    public void buildSuccessRes(T result,String requestId) {
        this.result = result;
        this.requestId = requestId;
        this.code = SUCCESS_CODE;
    }

    public void buildErrorResult(String requestId,String errorMessage) {
        this.requestId = requestId;
        this.errorMsg = errorMessage;
        this.code = FAIL_CODE;
    }
}

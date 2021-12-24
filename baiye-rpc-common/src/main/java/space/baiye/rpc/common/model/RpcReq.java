package space.baiye.rpc.common.model;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by Baiye on 2021/12/23.
 *
 * Client 向 Server 发送请求的数据实体
 *
 * @author Baiye
 */
@Data
public class RpcReq {

    private String requestId;

    private String interfaceName;

    private String methodName;

    private List<Pair<Class<?>,Object>> parameterList;
}

package moe.caa.multilogin.api.internal.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.ApiStatus;

/**
 * 表示一堆对象
 */
@Data
@ApiStatus.Internal
@AllArgsConstructor
public class There<V1, V2, V3> {
    private final V1 value1;
    private final V2 value2;
    private final V3 value3;
}
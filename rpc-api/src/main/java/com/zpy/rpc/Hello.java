package com.zpy.rpc;

import lombok.*;

import java.io.Serializable;

/**
 * @author zhao peng yu
 * @version 1.0
 * @date 2021/1/13 17:10
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hello implements Serializable {
    private String message;
    private String description;
}

package com.github.ipecter.rtuserver.cash.cash;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerCash {

    private final String name;
    private final int cash;

}

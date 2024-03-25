package com.github.ipecter.rtuserver.cash.cash;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Coin {

    private final String cash;
    private final String item;
    private final int value;

}

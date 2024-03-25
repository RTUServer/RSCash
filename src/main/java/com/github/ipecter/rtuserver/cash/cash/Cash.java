package com.github.ipecter.rtuserver.cash.cash;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Cash {

    private final String name;
    private final String displayName;
    private final String description;
    private final int maxCash;

}

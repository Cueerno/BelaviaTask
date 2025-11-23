package com.radiuk.belavia_task;

import com.radiuk.belavia_task.config.TextDataEtlCliConfig;

public class Main {

    public static void main(String[] args) throws Exception {
        TextDataEtlCliConfig.buildTextDataEtlCli().runApp();
    }
}

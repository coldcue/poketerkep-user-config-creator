package com.botcreator.stage;

import com.botcreator.WebRegisterInstance;

import java.util.logging.Logger;

abstract class StageImpl implements Stage {
    protected final Logger logger;
    protected final WebRegisterInstance wri;

    protected StageImpl(WebRegisterInstance wri) {
        this.wri = wri;
        this.logger = wri.getLogger();
    }
}

package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;
import org.junit.Before;
import org.junit.Test;

public class GetFacebookDataStageTest {

    private WebRegisterInstance wri;
    private GetFacebookDataStage getFacebookDataStage;

    @Before
    public void setUp() throws Exception {
        wri = new WebRegisterInstance(9060);
        getFacebookDataStage = new GetFacebookDataStage(wri);
    }

    @Test
    public void test() throws Exception {
        getFacebookDataStage.run();
    }
}
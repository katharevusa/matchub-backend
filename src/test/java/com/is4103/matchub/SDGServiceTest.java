/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub;

import com.is4103.matchub.entity.SDGEntity;
import com.is4103.matchub.service.SDGService;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ngjin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SDGServiceTest {

    public SDGServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Autowired
    private SDGService sDGService;

    @Test
    @Transactional
    public void testGetAllSdgs() {
        Pageable pageable = PageRequest.of(0, 200);
        List<SDGEntity> results = sDGService.getAllSdgs(pageable).getContent();

        assertFalse(results.isEmpty());
    }
}

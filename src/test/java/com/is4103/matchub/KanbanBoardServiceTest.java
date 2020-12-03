/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub;

import com.is4103.matchub.entity.KanbanBoardEntity;
import com.is4103.matchub.exception.KanbanBoardNotFoundException;
import com.is4103.matchub.exception.ProjectNotFoundException;
import com.is4103.matchub.repository.KanbanBoardEntityRepository;
import com.is4103.matchub.service.KanbanBoardServiceImpl;
import com.is4103.matchub.vo.KanbanBoardVO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author markt
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestMethodOrder(OrderAnnotation.class)
public class KanbanBoardServiceTest {

    public KanbanBoardServiceTest() {
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
    private KanbanBoardServiceImpl kanbanBoardServiceImpl;

    @Autowired
    private KanbanBoardEntityRepository kanbanBoardEntityRepository;
    KanbanBoardEntity newKanbanBoard;

    @Test
    @Order(1)
    public void testCreateKanbanBoard() {
        KanbanBoardVO vo = new KanbanBoardVO();
        vo.setChannelUid("321");
        vo.setProjectId(1L);
        newKanbanBoard = kanbanBoardServiceImpl.createKanbanBoard(vo);
        Assert.assertNotNull(newKanbanBoard);
    }

    @Test
    public void testGetKanbanBoardByKanbanBoardId() {
        try {
            KanbanBoardEntity result = kanbanBoardServiceImpl.getKanbanBoardByKanbanBoardId(1L);
            Assert.assertNotNull(result);
        } catch (KanbanBoardNotFoundException ex) {
        }
    }

    @Test(expected = KanbanBoardNotFoundException.class)
    public void testGetKanbanBoardByKanbanBoardIdKanbanBoardNotFoundException
() throws KanbanBoardNotFoundException {
        KanbanBoardEntity result = kanbanBoardServiceImpl.getKanbanBoardByKanbanBoardId(5L);
    }

    @Test
    public void testGetKanbanBoardByChannelUId() {
        try {
            KanbanBoardEntity result = kanbanBoardServiceImpl.getKanbanBoardByChannelUid("321");
            Assert.assertNotNull(result);
        } catch (KanbanBoardNotFoundException ex) {
        }
    }

    @Test(expected = KanbanBoardNotFoundException.class)
    public void testGetKanbanBoardByChannelUIdKanbanBoardNotFoundException() throws KanbanBoardNotFoundException {
        KanbanBoardEntity result = kanbanBoardServiceImpl.getKanbanBoardByChannelUid("999");
    }

    @Test
    public void testGetAllKanbanBoardByProjectId() {
        try {
            List<KanbanBoardEntity> projectKanbanBoards = kanbanBoardServiceImpl.getAllKanbanBoardByProjectId(1L);
            Assert.assertFalse(projectKanbanBoards.isEmpty());
        } catch (ProjectNotFoundException ex) {
        }
    }

    @Test(expected = ProjectNotFoundException.class)
    public void testGetAllKanbanBoardByProjectIdProjectNotFoundException() throws ProjectNotFoundException {
        List<KanbanBoardEntity> projectKanbanBoards = kanbanBoardServiceImpl.getAllKanbanBoardByProjectId(30L);
    }

    @Test
    public void testGetAllLabelsByKanbanBoardId() {
        Map<String, String> labels = kanbanBoardServiceImpl.getAllLabelsByKanbanBoardId(1L);
        Assert.assertNotNull(labels);
    }
}

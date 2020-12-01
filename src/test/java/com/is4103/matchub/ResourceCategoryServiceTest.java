/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub;

import com.is4103.matchub.entity.ResourceCategoryEntity;
import com.is4103.matchub.exception.DeleteResourceCategoryException;
import com.is4103.matchub.exception.ResourceCategoryNotFoundException;
import com.is4103.matchub.repository.ResourceCategoryEntityRepository;
import com.is4103.matchub.service.ResourceCategoryServiceImpl;
import com.is4103.matchub.vo.ResourceCategoryVO;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author kaikai
 */
@RunWith(SpringRunner.class)

@SpringBootTest
public class ResourceCategoryServiceTest {

    public ResourceCategoryServiceTest() {
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
    private ResourceCategoryServiceImpl resourceCategoryServiceImpl;
    ResourceCategoryEntity testResourceCategory;
    ResourceCategoryVO resourceCategoryVO;
    ResourceCategoryEntityRepository resourceCategoryEntityRepository;

    @Test
    public void testCreateResourceCategory() {
        testResourceCategory = new ResourceCategoryEntity();
        testResourceCategory.setResourceCategoryId(20l);
        testResourceCategory.setResourceCategoryName("testCategory");
        testResourceCategory.setResourceCategoryDescription("testing description");
        testResourceCategory.setCommunityPointsGuideline(5);
        testResourceCategory.setPerUnit(1);
        testResourceCategory.setUnitName("test");
        resourceCategoryServiceImpl.createResourceCategory(testResourceCategory);
        Assert.assertTrue(testResourceCategory.getResourceCategoryId() != null);
    }

    @Test
    public void testGetResourceCategoryById() {
        ResourceCategoryEntity rce = resourceCategoryServiceImpl.getResourceCategoryById(1l);
        Assert.assertTrue(rce.getResourceCategoryId() != null);
    }

    @Test
    public void testGetAllResourceCategories() {
        Page<ResourceCategoryEntity> allResourceCategory = resourceCategoryServiceImpl.getAllResourceCategories(PageRequest.of(0, 5));
        Assert.assertTrue(allResourceCategory.getTotalElements() != 0);
    }

    @Test
    public void testUpdateResourceCategory() {
        resourceCategoryVO = new ResourceCategoryVO();
        resourceCategoryVO.setResourceCategoryName("testCategoryUpdate");
        resourceCategoryVO.setResourceCategoryDescription("testing description");
        resourceCategoryVO.setCommunityPointsGuideline(5);
        resourceCategoryVO.setPerUnit(1);
        resourceCategoryVO.setUnitName("test");
        resourceCategoryVO.setResourceCategoryId(2l);
        try {
            resourceCategoryServiceImpl.updateResourceCategory(resourceCategoryVO);
            Assert.assertTrue(resourceCategoryVO.getResourceCategoryName().equals("testCategoryUpdate"));
        } catch (ResourceCategoryNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Test(expected = ResourceCategoryNotFoundException.class)
    public void testUpdateResourceCategoryResourceCategoryNotFound() throws ResourceCategoryNotFoundException {
        resourceCategoryVO = new ResourceCategoryVO();
        resourceCategoryVO.setResourceCategoryId(30l);
        resourceCategoryVO.setResourceCategoryName("testCategoryUpdate");
        resourceCategoryVO.setResourceCategoryDescription("testing description");
        resourceCategoryVO.setCommunityPointsGuideline(5);
        resourceCategoryVO.setPerUnit(1);
        resourceCategoryVO.setUnitName("test");
        resourceCategoryServiceImpl.updateResourceCategory(resourceCategoryVO);
    }

    @Test
    public void testDeleteResourceCategories() {
        try {
            resourceCategoryServiceImpl.deleteResourceCategories(1l);
            Assert.assertFalse(resourceCategoryEntityRepository.findById(1l).isPresent());
        } catch (ResourceCategoryNotFoundException ex) {
            ex.printStackTrace();
        } catch (DeleteResourceCategoryException ex) {
            ex.printStackTrace();
        }
    }

    @Test(expected = ResourceCategoryNotFoundException.class)
    public void testDeleteResourceCategoriesResourceCategoryNotFound() throws ResourceCategoryNotFoundException {
        try {
            resourceCategoryServiceImpl.deleteResourceCategories(30l);
        } catch (DeleteResourceCategoryException ex) {
            ex.printStackTrace();
        }

    }
     @Test(expected = DeleteResourceCategoryException.class)
    public void testDeleteResourceCategoriesDeleteResourceCategoryException() throws DeleteResourceCategoryException {
        try {
            resourceCategoryServiceImpl.deleteResourceCategories(1l);
        } catch (ResourceCategoryNotFoundException ex) {
            ex.printStackTrace();
        }

    }
}

package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AdminRouterControllerUnitTest {

  private AdminRouterController controller;

  @BeforeEach
  void setUp() {
    controller = new AdminRouterController();
  }

  @Test
  void viewMappings_shouldReturnExpectedTemplates() {
    assertEquals("admin/user", controller.toUserManage());
    assertEquals("admin/books", controller.toBookManage());
    assertEquals("admin/category", controller.toCategoryManage());
    assertEquals("admin/order", controller.toOrderManage());
    assertEquals("admin/add_book", controller.AddBook());
    assertEquals("admin/supplier", controller.toSupplierManage());
    assertEquals("admin/purchase", controller.toPurchaseManage());
    assertEquals("admin/inventory", controller.toInventoryManage());
    assertEquals("admin/admin", controller.toAdminManage());
    assertEquals("admin/login", controller.toAdminLogin());
  }
}


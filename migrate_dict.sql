USE ems_db;

DELETE FROM sys_dictionary;

INSERT INTO sys_dictionary (`type`, `code`, `name`, `sort`, `parent_code`) VALUES
('department', 'tech', '技术部', 1, NULL),
('department', 'product', '产品部', 2, NULL),
('department', 'design', '设计部', 3, NULL),
('department', 'operation', '运营部', 4, NULL),
('department', 'hr', '人事部', 5, NULL),
('department', 'finance', '财务部', 6, NULL),
('position', 'java_dev', 'Java开发', 1, 'tech'),
('position', 'frontend_dev', '前端开发', 2, 'tech'),
('position', 'product_manager', '产品经理', 3, 'product'),
('position', 'ui_designer', 'UI设计师', 4, 'design'),
('position', 'qa_engineer', '测试工程师', 5, 'tech'),
('position', 'operation_specialist', '运营专员', 6, 'operation'),
('position', 'hr_supervisor', '人事主管', 7, 'hr'),
('position', 'finance_specialist', '财务专员', 8, 'finance'),
('rank', 'P5', 'P5', 1, NULL),
('rank', 'P6', 'P6', 2, NULL),
('rank', 'P7', 'P7', 3, NULL),
('rank', 'P8', 'P8', 4, NULL);

UPDATE employee SET `department` = CASE `department`
  WHEN '技术部' THEN 'tech'
  WHEN '产品部' THEN 'product'
  WHEN '设计部' THEN 'design'
  WHEN '运营部' THEN 'operation'
  WHEN '人事部' THEN 'hr'
  WHEN '财务部' THEN 'finance'
  ELSE `department`
END;

UPDATE employee SET `position` = CASE `position`
  WHEN 'Java开发' THEN 'java_dev'
  WHEN '前端开发' THEN 'frontend_dev'
  WHEN '产品经理' THEN 'product_manager'
  WHEN 'UI设计师' THEN 'ui_designer'
  WHEN '测试工程师' THEN 'qa_engineer'
  WHEN '运营专员' THEN 'operation_specialist'
  WHEN '人事主管' THEN 'hr_supervisor'
  WHEN '财务专员' THEN 'finance_specialist'
  ELSE `position`
END;

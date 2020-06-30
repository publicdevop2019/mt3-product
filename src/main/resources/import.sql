INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(0,'手机数码',null,'分类:手机','FRONTEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(5,'平板电脑',null,'分类:平板','FRONTEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(10,'台式电脑',null,'分类:台式','FRONTEND');

INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(15,'华为',null,'品牌:华为','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(16,'苹果',null,'品牌:苹果','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(17,'小米',null,'品牌:小米','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(18,'三星',null,'品牌:三星','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(19,'手机数码',15,'分类:手机','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(21,'台式电脑',15,'分类:台式','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(22,'手机数码',16,'分类:手机','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(23,'平板电脑',16,'分类:平板','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(24,'台式电脑',16,'分类:台式','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(31,'无线耳机',16,'分类:无线耳机','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(25,'手机数码',17,'分类:手机','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(26,'平板电脑',17,'分类:平板','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(27,'台式电脑',17,'分类:台式','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(28,'手机数码',18,'分类:手机','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(29,'平板电脑',18,'分类:平板','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(30,'智能电视',18,'分类:智能电视','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(32,'60寸',30,'屏幕尺寸:60英尺','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(33,'50寸',30,'屏幕尺寸:50英尺','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(34,'40寸',30,'屏幕尺寸:40英尺','BACKEND');
INSERT INTO  biz_catalog (id,name,parent_id,attributes,type) VALUES(35,'笔记本电脑',16,'品牌:苹果,分类:笔记本','BACKEND');

INSERT INTO  biz_attribute (id,name,method,select_values,type) VALUES(0,'品牌','SELECT','华为,苹果,小米,三星','KEY_ATTR');
INSERT INTO  biz_attribute (id,name,method,select_values,type) VALUES(1,'分类','SELECT','台式,平板,笔记本,手机,无线耳机,智能电视','KEY_ATTR');
INSERT INTO  biz_attribute (id,name,method,select_values,type) VALUES(2,'屏幕尺寸','SELECT','6.0英尺,5.0英尺,4.0英尺','PROD_ATTR');
INSERT INTO  biz_attribute (id,name,method,select_values,type) VALUES(3,'新款','SELECT','是,否','GEN_ATTR');
INSERT INTO  biz_attribute (id,name,method,select_values,type) VALUES(4,'内存','SELECT','64g,128g,256g','SALES_ATTR');
INSERT INTO  biz_attribute (id,name,method,select_values,type) VALUES(5,'分辨率','SELECT','1080px,1920px,2340px,3840px','PROD_ATTR');
INSERT INTO  biz_attribute (id,name,method,select_values,type) VALUES(6,'摄像头','SELECT','5M,10M,20M','PROD_ATTR');
INSERT INTO  biz_attribute (id,name,method,select_values,type) VALUES(7,'颜色','SELECT','玫瑰红,土豪金,天空银,夜空黑','SALES_ATTR');


INSERT INTO product_detail (id,created_at,created_by,modified_at,modified_by,attr_gen,attr_key,attr_prod,description,image_url_large,image_url_small,name,selected_options,specification,status) VALUES(835343040929792,'2020-06-27 20:40:08','0','2020-06-27 20:43:31','0','新款:是','分类:手机,品牌:华为','摄像头:20M,屏幕尺寸:5.0英尺,分辨率:3840px','测试','','','测试华为手机','个性定制:光刻签名&+100=七天无理由退货&+150=清理工具包&+50',NULL,'AVAILABLE');
INSERT INTO product_detail (id,created_at,created_by,modified_at,modified_by,attr_gen,attr_key,attr_prod,description,image_url_large,image_url_small,name,selected_options,specification,status) VALUES(835343559974912,'2020-06-27 20:56:37','0','2020-06-27 20:56:37','0','新款:是','分类:台式,品牌:华为','屏幕尺寸:6.0英尺','',NULL,'','测试台式电脑',NULL,NULL,'AVAILABLE');
INSERT INTO product_detail (id,created_at,created_by,modified_at,modified_by,attr_gen,attr_key,attr_prod,description,image_url_large,image_url_small,name,selected_options,specification,status) VALUES(835343576227840,'2020-06-27 20:57:09','0','2020-06-27 20:57:09','0','新款:是','品牌:苹果,分类:平板','屏幕尺寸:6.0英尺','',NULL,'','测试平板',NULL,NULL,'AVAILABLE');
INSERT INTO product_sku_map (product_id,attributes_sales,price,sales,storage_actual,storage_order) VALUES(835343040929792,'内存:64g,颜色:天空银',3999.00,0,100,101);
INSERT INTO product_sku_map (product_id,attributes_sales,price,sales,storage_actual,storage_order) VALUES(835343040929792,'内存:128g,颜色:玫瑰红',4199.00,0,200,202);
INSERT INTO product_sku_map (product_id,attributes_sales,price,sales,storage_actual,storage_order) VALUES(835343040929792,'内存:64g,颜色:玫瑰红',4099.00,0,300,303);
INSERT INTO product_sku_map (product_id,attributes_sales,price,sales,storage_actual,storage_order) VALUES(835343559974912,'内存:256g',12999.00,0,900,1000);
INSERT INTO product_sku_map (product_id,attributes_sales,price,sales,storage_actual,storage_order) VALUES(835343576227840,'内存:128g',15999.00,0,800,900);


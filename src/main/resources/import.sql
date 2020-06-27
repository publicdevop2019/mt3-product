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
INSERT INTO  biz_attribute (id,name,method,select_values,type) VALUES(2,'屏幕尺寸','SELECT','60英尺,50英尺,40英尺','PROD_ATTR');
INSERT INTO  biz_attribute (id,name,method,select_values,type) VALUES(3,'新款','SELECT','是,否','GEN_ATTR');
INSERT INTO  biz_attribute (id,name,method,select_values,type) VALUES(4,'内存','SELECT','64g,128g,256g','SALES_ATTR');


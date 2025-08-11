-- 1 Create the PROPERTY record for the Palo Escrito BRANCH
INSERT INTO PROPERTY (
    Country,
    State,
    Municipality,
    Postal_Code,
    Neighborhood,
    Address_Detail
) VALUES (
    'México',
    'Morelos',
    'Emiliano Zapata',
    62765,
    'Palo Escrito',
    'Calle Jacarandas 123'
);

-- 2 Create the record in BRANCH with a unique key
INSERT INTO BRANCH (
    Id_Property,
    Branch_Code,
    Branch_Registration_Date,
    Is_Deleted
) VALUES (
    (SELECT Id_Property 
        FROM PROPERTY 
        WHERE Neighborhood = 'Palo Escrito' 
    ),
    'A1-20250811-1234',
    SYSDATE,
    0
);

-- 3 Insert and link the 12 WAREHOUSES to the Palo Escrito BRANCH
INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status) 
VALUES (
    (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    (SELECT Id_Branch FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    'A1-20250811-1234-1', SYSDATE, 'Warehouse A1', 'Warehouse1.jpg', 2400.00, 480000.00, 1200.50, 'Available'
);

INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status) 
VALUES (
    (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    (SELECT Id_Branch FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    'A1-20250811-1234-2', SYSDATE, 'Warehouse A2', 'Warehouse2.jpg', 3700.00, 740000.00, 1850.25, 'Rented'
);

INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status) 
VALUES (
    (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    (SELECT Id_Branch FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    'A1-20250811-1234-3', SYSDATE, 'Warehouse A3', 'Warehouse3.jpg', 3000.00, 600000.00, 1500.00, 'Sold'
);

INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status) 
VALUES (
    (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    (SELECT Id_Branch FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    'A1-20250811-1234-4', SYSDATE, 'Warehouse B1', 'Warehouse4.jpg', 4400.00, 880000.00, 2200.75, 'Rent Only'
);

INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status) 
VALUES (
    (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    (SELECT Id_Branch FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    'A1-20250811-1234-5', SYSDATE, 'Warehouse B2', 'Warehouse5.jpg', 1900.00, 380000.00, 950.80, 'Sale Only'
);

INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status) 
VALUES (
    (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    (SELECT Id_Branch FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    'A1-20250811-1234-6', SYSDATE, 'Warehouse B3', 'Warehouse6.jpg', 6800.00, 1140000.00, 2850.60, 'Available'
);

INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status) 
VALUES (
    (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    (SELECT Id_Branch FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    'A1-20250811-1234-7', SYSDATE, 'Warehouse C1', 'Warehouse7.jpg', 3360.00, 672000.00, 1680.40, 'Rented'
);

INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status) 
VALUES (
    (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    (SELECT Id_Branch FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    'A1-20250811-1234-8', SYSDATE, 'Warehouse C2', 'Warehouse8.jpg', 2850.00, 570000.00, 1425.30, 'Available'
);

INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status) 
VALUES (
    (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    (SELECT Id_Branch FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    'A1-20250811-1234-9', SYSDATE, 'Warehouse C3', 'Warehouse9.jpg', 4200.00, 840000.00, 2100.85, 'Rent Only'
);

INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status) 
VALUES (
    (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    (SELECT Id_Branch FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    'A1-20250811-1234-10', SYSDATE, 'Warehouse D1', 'Warehouse10.jpg', 3900.00, 780000.00, 1950.20, 'Sale Only'
);

INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status) 
VALUES (
    (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    (SELECT Id_Branch FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    'A1-20250811-1234-11', SYSDATE, 'Warehouse D2', 'Warehouse11.jpg', 2640.00, 528000.00, 1320.75, 'Sold'
);

INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status) 
VALUES (
    (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    (SELECT Id_Branch FROM BRANCH WHERE Branch_Code = 'A1-20250811-1234'),
    'A1-20250811-1234-12', SYSDATE, 'Warehouse D3', 'Warehouse12.jpg', 3560.00, 712000.00, 1780.90, 'Available'
);

-- Oracle SQL

/* =============================
   Sede B1 - CDMX, Tlalpan
   ============================= */
-- PROPERTY
INSERT INTO PROPERTY (Country, State, Municipality, Postal_Code, Neighborhood, Address_Detail)
VALUES ('México', 'Ciudad de México', 'Tlalpan', 14080, 'Coapa', 'Av. Acoxpa 456');

-- BRANCH
INSERT INTO BRANCH (Id_Property, Branch_Code, Branch_Registration_Date, Is_Deleted)
VALUES (
           (SELECT Id_Property FROM PROPERTY WHERE Neighborhood = 'Coapa' AND Address_Detail = 'Av. Acoxpa 456'),
           'B1-20250811-2001',
           SYSDATE,
           0
       );

-- WAREHOUSES (5 Available, 1 Rented)
INSERT INTO WAREHOUSE (Id_Property, Id_Branch, Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status)
VALUES (
           (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B1-20250811-2001'),
           (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B1-20250811-2001'),
           'B1-20250811-2001-1', SYSDATE, 'Warehouse B1-1', 'Warehouse1.jpg', 1800.00, 360000.00, 800.00, 'Available'
       );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B1-20250811-2001-2', SYSDATE, 'Warehouse B1-2', 'Warehouse2.jpg', 1850.00, 370000.00, 820.00, 'Available',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B1-20250811-2001'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B1-20250811-2001')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B1-20250811-2001-3', SYSDATE, 'Warehouse B1-3', 'Warehouse3.jpg', 1900.00, 380000.00, 840.00, 'Available',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B1-20250811-2001'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B1-20250811-2001')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B1-20250811-2001-4', SYSDATE, 'Warehouse B1-4', 'Warehouse4.jpg', 1950.00, 390000.00, 860.00, 'Available',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B1-20250811-2001'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B1-20250811-2001')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B1-20250811-2001-5', SYSDATE, 'Warehouse B1-5', 'Warehouse5.jpg', 2000.00, 400000.00, 880.00, 'Available',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B1-20250811-2001'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B1-20250811-2001')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B1-20250811-2001-6', SYSDATE, 'Warehouse B1-6', 'Warehouse6.jpg', 2050.00, 410000.00, 900.00, 'Rented',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B1-20250811-2001'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B1-20250811-2001')
                             );

/* =============================
   Sede B2 - Jalisco, Zapopan
   ============================= */
-- PROPERTY
INSERT INTO PROPERTY (Country, State, Municipality, Postal_Code, Neighborhood, Address_Detail)
VALUES ('México', 'Jalisco', 'Zapopan', 45130, 'Puerta de Hierro', 'Blvd. Puerta de Hierro 101');

-- BRANCH
INSERT INTO BRANCH (Id_Property, Branch_Code, Branch_Registration_Date, Is_Deleted)
VALUES (
           (SELECT Id_Property FROM PROPERTY WHERE Neighborhood = 'Puerta de Hierro' AND Address_Detail = 'Blvd. Puerta de Hierro 101'),
           'B2-20250811-2002',
           SYSDATE,
           0
       );

-- WAREHOUSES (3 Available, 2 Rented, 1 Sold)
INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B2-20250811-2002-1', SYSDATE, 'Warehouse B2-1', 'Warehouse1.jpg', 2100.00, 420000.00, 820.00, 'Available',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B2-20250811-2002'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B2-20250811-2002')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B2-20250811-2002-2', SYSDATE, 'Warehouse B2-2', 'Warehouse2.jpg', 2150.00, 430000.00, 840.00, 'Available',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B2-20250811-2002'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B2-20250811-2002')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B2-20250811-2002-3', SYSDATE, 'Warehouse B2-3', 'Warehouse3.jpg', 2200.00, 440000.00, 860.00, 'Available',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B2-20250811-2002'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B2-20250811-2002')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B2-20250811-2002-4', SYSDATE, 'Warehouse B2-4', 'Warehouse4.jpg', 2250.00, 450000.00, 880.00, 'Rented',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B2-20250811-2002'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B2-20250811-2002')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B2-20250811-2002-5', SYSDATE, 'Warehouse B2-5', 'Warehouse5.jpg', 2300.00, 460000.00, 900.00, 'Rented',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B2-20250811-2002'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B2-20250811-2002')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B2-20250811-2002-6', SYSDATE, 'Warehouse B2-6', 'Warehouse6.jpg', 2350.00, 470000.00, 920.00, 'Sold',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B2-20250811-2002'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B2-20250811-2002')
                             );

/* =============================
   Sede B3 - Puebla, Puebla
   ============================= */
-- PROPERTY
INSERT INTO PROPERTY (Country, State, Municipality, Postal_Code, Neighborhood, Address_Detail)
VALUES ('México', 'Puebla', 'Puebla', 72000, 'Angelópolis', 'Vía Atlixcáyotl 500');

-- BRANCH
INSERT INTO BRANCH (Id_Property, Branch_Code, Branch_Registration_Date, Is_Deleted)
VALUES (
           (SELECT Id_Property FROM PROPERTY WHERE Neighborhood = 'Angelópolis' AND Address_Detail = 'Vía Atlixcáyotl 500'),
           'B3-20250811-2003',
           SYSDATE,
           0
       );

-- WAREHOUSES (2 Available, 2 Rent Only, 2 Sale Only)
INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B3-20250811-2003-1', SYSDATE, 'Warehouse B3-1', 'Warehouse1.jpg', 1900.00, 380000.00, 780.00, 'Available',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B3-20250811-2003'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B3-20250811-2003')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B3-20250811-2003-2', SYSDATE, 'Warehouse B3-2', 'Warehouse2.jpg', 1950.00, 390000.00, 800.00, 'Available',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B3-20250811-2003'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B3-20250811-2003')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B3-20250811-2003-3', SYSDATE, 'Warehouse B3-3', 'Warehouse3.jpg', 2000.00, 400000.00, 820.00, 'Rent Only',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B3-20250811-2003'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B3-20250811-2003')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B3-20250811-2003-4', SYSDATE, 'Warehouse B3-4', 'Warehouse4.jpg', 2050.00, 410000.00, 840.00, 'Rent Only',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B3-20250811-2003'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B3-20250811-2003')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B3-20250811-2003-5', SYSDATE, 'Warehouse B3-5', 'Warehouse5.jpg', 2100.00, 420000.00, 860.00, 'Sale Only',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B3-20250811-2003'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B3-20250811-2003')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B3-20250811-2003-6', SYSDATE, 'Warehouse B3-6', 'Warehouse6.jpg', 2150.00, 430000.00, 880.00, 'Sale Only',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B3-20250811-2003'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B3-20250811-2003')
                             );

/* =============================
   Sede B4 - Edo. México, Toluca
   ============================= */
-- PROPERTY
INSERT INTO PROPERTY (Country, State, Municipality, Postal_Code, Neighborhood, Address_Detail)
VALUES ('México', 'Estado de México', 'Toluca', 50090, 'Centro', 'Calle 16 de Septiembre 99');

-- BRANCH
INSERT INTO BRANCH (Id_Property, Branch_Code, Branch_Registration_Date, Is_Deleted)
VALUES (
           (SELECT Id_Property FROM PROPERTY WHERE Neighborhood = 'Centro' AND Address_Detail = 'Calle 16 de Septiembre 99'),
           'B4-20250811-2004',
           SYSDATE,
           0
       );

-- WAREHOUSES (0 Available, 3 Rented, 3 Sold)
INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B4-20250811-2004-1', SYSDATE, 'Warehouse B4-1', 'Warehouse1.jpg', 1700.00, 340000.00, 760.00, 'Rented',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B4-20250811-2004'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B4-20250811-2004')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B4-20250811-2004-2', SYSDATE, 'Warehouse B4-2', 'Warehouse2.jpg', 1750.00, 350000.00, 780.00, 'Rented',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B4-20250811-2004'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B4-20250811-2004')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B4-20250811-2004-3', SYSDATE, 'Warehouse B4-3', 'Warehouse3.jpg', 1800.00, 360000.00, 800.00, 'Rented',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B4-20250811-2004'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B4-20250811-2004')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B4-20250811-2004-4', SYSDATE, 'Warehouse B4-4', 'Warehouse4.jpg', 1850.00, 370000.00, 820.00, 'Sold',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B4-20250811-2004'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B4-20250811-2004')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B4-20250811-2004-5', SYSDATE, 'Warehouse B4-5', 'Warehouse5.jpg', 1900.00, 380000.00, 840.00, 'Sold',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B4-20250811-2004'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B4-20250811-2004')
                             );

INSERT INTO WAREHOUSE VALUES (
                                 DEFAULT,
                                 'B4-20250811-2004-6', SYSDATE, 'Warehouse B4-6', 'Warehouse6.jpg', 1950.00, 390000.00, 860.00, 'Sold',
                                 0,
                                 (SELECT Id_Property FROM BRANCH WHERE Branch_Code = 'B4-20250811-2004'),
                                 (SELECT Id_Branch   FROM BRANCH WHERE Branch_Code = 'B4-20250811-2004')
                             );
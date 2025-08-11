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
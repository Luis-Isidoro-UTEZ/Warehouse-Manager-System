-- ============================================
-- SCRIPT 3: Insertar Warehouse Transactions
-- ============================================

-- Palo Escrito (A1)
INSERT INTO WAREHOUSE_TRANSACTION (Transaction_Type, Transaction_Date, Payment_Expiration_Date, Id_Warehouse, Id_Client, Id_Admin)
SELECT 'Rent', SYSDATE, DATE '2025-09-19', w.Id_Warehouse, w.Id_Client, a.Id_Admin
FROM WAREHOUSE w
JOIN ADMINISTRATOR a ON a.Id_Branch = w.Id_Branch
WHERE w.Warehouse_Code = 'A1-20250811-1234-2';

INSERT INTO WAREHOUSE_TRANSACTION (Transaction_Type, Transaction_Date, Payment_Expiration_Date, Id_Warehouse, Id_Client, Id_Admin)
SELECT 'Sale', SYSDATE, NULL, w.Id_Warehouse, w.Id_Client, a.Id_Admin
FROM WAREHOUSE w
JOIN ADMINISTRATOR a ON a.Id_Branch = w.Id_Branch
WHERE w.Warehouse_Code = 'A1-20250811-1234-3';

INSERT INTO WAREHOUSE_TRANSACTION (Transaction_Type, Transaction_Date, Payment_Expiration_Date, Id_Warehouse, Id_Client, Id_Admin)
SELECT 'Rent', SYSDATE, DATE '2025-09-19', w.Id_Warehouse, w.Id_Client, a.Id_Admin
FROM WAREHOUSE w
JOIN ADMINISTRATOR a ON a.Id_Branch = w.Id_Branch
WHERE w.Warehouse_Code = 'A1-20250811-1234-7';

INSERT INTO WAREHOUSE_TRANSACTION (Transaction_Type, Transaction_Date, Payment_Expiration_Date, Id_Warehouse, Id_Client, Id_Admin)
SELECT 'Sale', SYSDATE, NULL, w.Id_Warehouse, w.Id_Client, a.Id_Admin
FROM WAREHOUSE w
JOIN ADMINISTRATOR a ON a.Id_Branch = w.Id_Branch
WHERE w.Warehouse_Code = 'A1-20250811-1234-11';

-- Coapa (B1)
INSERT INTO WAREHOUSE_TRANSACTION (Transaction_Type, Transaction_Date, Payment_Expiration_Date, Id_Warehouse, Id_Client, Id_Admin)
SELECT 'Rent', SYSDATE, DATE '2025-09-19', w.Id_Warehouse, w.Id_Client, a.Id_Admin
FROM WAREHOUSE w
JOIN ADMINISTRATOR a ON a.Id_Branch = w.Id_Branch
WHERE w.Warehouse_Code = 'B1-20250811-2001-6';

-- Puerta de Hierro (B2)
INSERT INTO WAREHOUSE_TRANSACTION (Transaction_Type, Transaction_Date, Payment_Expiration_Date, Id_Warehouse, Id_Client, Id_Admin)
SELECT 'Rent', SYSDATE, DATE '2025-09-19', w.Id_Warehouse, w.Id_Client, a.Id_Admin
FROM WAREHOUSE w
JOIN ADMINISTRATOR a ON a.Id_Branch = w.Id_Branch
WHERE w.Warehouse_Code = 'B2-20250811-2002-4';

INSERT INTO WAREHOUSE_TRANSACTION (Transaction_Type, Transaction_Date, Payment_Expiration_Date, Id_Warehouse, Id_Client, Id_Admin)
SELECT 'Rent', SYSDATE, DATE '2025-09-19', w.Id_Warehouse, w.Id_Client, a.Id_Admin
FROM WAREHOUSE w
JOIN ADMINISTRATOR a ON a.Id_Branch = w.Id_Branch
WHERE w.Warehouse_Code = 'B2-20250811-2002-5';

INSERT INTO WAREHOUSE_TRANSACTION (Transaction_Type, Transaction_Date, Payment_Expiration_Date, Id_Warehouse, Id_Client, Id_Admin)
SELECT 'Sale', SYSDATE, NULL, w.Id_Warehouse, w.Id_Client, a.Id_Admin
FROM WAREHOUSE w
JOIN ADMINISTRATOR a ON a.Id_Branch = w.Id_Branch
WHERE w.Warehouse_Code = 'B2-20250811-2002-6';

-- Toluca (B4)
INSERT INTO WAREHOUSE_TRANSACTION (Transaction_Type, Transaction_Date, Payment_Expiration_Date, Id_Warehouse, Id_Client, Id_Admin)
SELECT 'Rent', SYSDATE, DATE '2025-09-19', w.Id_Warehouse, w.Id_Client, a.Id_Admin
FROM WAREHOUSE w
JOIN ADMINISTRATOR a ON a.Id_Branch = w.Id_Branch
WHERE w.Warehouse_Code IN ('B4-20250811-2004-1','B4-20250811-2004-2','B4-20250811-2004-3');

INSERT INTO WAREHOUSE_TRANSACTION (Transaction_Type, Transaction_Date, Payment_Expiration_Date, Id_Warehouse, Id_Client, Id_Admin)
SELECT 'Sale', SYSDATE, NULL, w.Id_Warehouse, w.Id_Client, a.Id_Admin
FROM WAREHOUSE w
JOIN ADMINISTRATOR a ON a.Id_Branch = w.Id_Branch
WHERE w.Warehouse_Code IN ('B4-20250811-2004-4','B4-20250811-2004-5','B4-20250811-2004-6');

COMMIT;

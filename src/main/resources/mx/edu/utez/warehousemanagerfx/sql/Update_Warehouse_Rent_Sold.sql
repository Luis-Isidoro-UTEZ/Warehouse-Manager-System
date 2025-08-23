-- ============================================
-- SCRIPT 2: Actualizar warehouses con Id_Client
-- ============================================

-- Palo Escrito (A1)
UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='juan.perez@example.com')
WHERE Warehouse_Code = 'A1-20250811-1234-2';

UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='maria.lopez@example.com')
WHERE Warehouse_Code = 'A1-20250811-1234-3';

UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='carlos.hernandez@example.com')
WHERE Warehouse_Code = 'A1-20250811-1234-7';

UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='ana.ramirez@example.com')
WHERE Warehouse_Code = 'A1-20250811-1234-11';

-- Coapa (B1)
UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='jose.martinez@example.com')
WHERE Warehouse_Code = 'B1-20250811-2001-6';

-- Puerta de Hierro (B2)
UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='laura.garcia@example.com')
WHERE Warehouse_Code = 'B2-20250811-2002-4';

UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='juan.perez@example.com')
WHERE Warehouse_Code = 'B2-20250811-2002-5';

UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='maria.lopez@example.com')
WHERE Warehouse_Code = 'B2-20250811-2002-6';

-- Toluca (B4)
UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='carlos.hernandez@example.com')
WHERE Warehouse_Code = 'B4-20250811-2004-1';

UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='ana.ramirez@example.com')
WHERE Warehouse_Code = 'B4-20250811-2004-2';

UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='jose.martinez@example.com')
WHERE Warehouse_Code = 'B4-20250811-2004-3';

UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='laura.garcia@example.com')
WHERE Warehouse_Code = 'B4-20250811-2004-4';

UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='juan.perez@example.com')
WHERE Warehouse_Code = 'B4-20250811-2004-5';

UPDATE WAREHOUSE SET Id_Client = (SELECT Id_Client FROM CLIENT WHERE Email='maria.lopez@example.com')
WHERE Warehouse_Code = 'B4-20250811-2004-6';

COMMIT;

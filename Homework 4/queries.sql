-- Queries
-- ------------------------------------------------------------
-- a) Find all distinct track names that start with the letter Z and sort them alphabetically.
SELECT DISTINCT Name FROM Track WHERE Name LIKE 'Z%' ORDER BY Name;

-- b) Find the first name of employees who are older than their supervisors.
-- Compare the BirthDate of the employee wit the BirthDate of the Employee's supervisor. (ReportsTo attribute)
-- Sort alphabetically by first name of the employee.
SELECT e.FirstName, e.LastName, e.BirthDate
FROM Employee e
JOIN Employee s ON e.ReportsTo = s.EmployeeId
WHERE e.BirthDate > s.BirthDate
ORDER BY e.FirstName;

-- c) Find the name of the highest-priced track. If more than one track has the highest price, return the names of all such tracks. Sort the output alphabetically based on the track name.
SELECT Name FROM Track
WHERE UnitPrice = (SELECT MAX(UnitPrice) FROM Track)
ORDER BY Name;

-- d) List all customers by ID and last name as well as total amount spent per customer. Sort the output by total spent descending.
--  Include any customers who have never purchased anything.
SELECT c.CustomerId, c.LastName, SUM(il.UnitPrice * il.Quantity) AS TotalSpent
FROM Customer c
JOIN Invoice i ON c.CustomerId = i.CustomerId
JOIN InvoiceLine il ON i.InvoiceId = il.InvoiceId
GROUP BY c.CustomerId, c.LastName
HAVING SUM(il.UnitPrice * il.Quantity) >= 0
ORDER BY TotalSpent DESC;

-- e) Find the highest priced album by adding up the prices of all tracks on the album. Find the highest-priced album.
SELECT a.Title, SUM(t.UnitPrice) AS TotalPrice
FROM Album a
JOIN Track t ON a.AlbumId = t.AlbumId
GROUP BY a.Title
HAVING SUM(t.UnitPrice) = (SELECT MAX(TotalPrice) FROM (SELECT SUM(t.UnitPrice) AS TotalPrice
FROM Album a
JOIN Track t ON a.AlbumId = t.AlbumId
GROUP BY a.Title) AS MaxPrice);

-- f) Find all albums that have ALL tracks that do not have an invoice line and sort them by album title.
SELECT DISTINCT a.Title, a.AlbumId
FROM Album a
JOIN Track t ON a.AlbumId = t.AlbumId
LEFT JOIN InvoiceLine il ON t.TrackId = il.TrackId
WHERE il.InvoiceLineId IS NULL
ORDER BY a.Title;

-- g) Create a view that returns customers’ first and last names along with corresponding sums of all their invoice totals. Name the view as “CustomerInvoices.” 
CREATE VIEW CustomerInvoices AS
SELECT c.CustomerId, c.FirstName, c.LastName, SUM(il.UnitPrice * il.Quantity) AS TotalSpent
FROM Customer c
JOIN Invoice i ON c.CustomerId = i.CustomerId
JOIN InvoiceLine il ON i.InvoiceId = il.InvoiceId
GROUP BY c.CustomerId, c.FirstName, c.LastName;
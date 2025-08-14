# Product Service Database Schema

## MongoDB Collections

### products
Main collection for storing product information.

```javascript
{
  "_id": ObjectId,
  "code": "PROD2024001",           // Internal product code
  "sku": "LAPTOP-001",             // Stock Keeping Unit
  "title": "MacBook Pro 14",
  "subtitle": "Apple M3 Pro",
  "description": "Latest MacBook Pro with M3 Pro chip",
  "type": "ELECTRONICS",
  
  // Manufacturing info
  "manufacturer": {
    "id": "APPLE-001",
    "name": "Apple Inc",
    "contactInfo": {...}
  },
  "manufacturedYear": 2024,
  "releaseDate": ISODate,
  "version": "2024",
  
  // Physical properties
  "physical": {
    "format": "LAPTOP",
    "language": "EN",
    "dimensions": {...},
    "weight": 1.6
  },
  
  // Suppliers
  "suppliers": [{
    "id": "SUP001",
    "name": "Tech Distributor",
    "role": "PRIMARY"
  }],
  
  // Categories
  "categories": [{
    "id": "CAT001", 
    "name": "Electronics",
    "path": "/electronics/computers/laptops"
  }],
  
  // Pricing
  "pricing": {
    "listPrice": 2499.99,
    "salePrice": 2299.99,
    "currency": "USD"
  },
  
  // Images
  "images": {
    "thumbnail": "url",
    "main": ["url1", "url2"],
    "gallery": ["url3", "url4"]
  },
  
  // Rating & Reviews
  "rating": {
    "average": 4.5,
    "count": 123,
    "distribution": {
      "5": 80, "4": 25, "3": 10, "2": 5, "1": 3
    }
  },
  
  // Stock & Availability
  "stockQuantity": 50,
  "reservedQuantity": 5,
  "availableQuantity": 45,
  "availability": "IN_STOCK",
  "status": "ACTIVE",
  
  // Business data
  "featured": {...},
  "sales": {...},
  "seo": {...},
  "tags": ["laptop", "apple", "m3"],
  
  // Audit
  "createdAt": ISODate,
  "updatedAt": ISODate
}
```

### product_series
Collection for managing product series/collections.

```javascript
{
  "_id": ObjectId,
  "name": "MacBook Pro Series",
  "description": "Professional laptop series",
  "manufacturer": {...},
  "totalProducts": 5,
  "status": "ACTIVE",
  "createdAt": ISODate,
  "updatedAt": ISODate
}
```

## Indexes

### products collection
- `{ "sku": 1 }` - unique
- `{ "code": 1 }` - unique  
- `{ "title": "text", "description": "text" }` - text search
- `{ "categories.id": 1 }`
- `{ "status": 1, "availability": 1 }`
- `{ "pricing.salePrice": 1 }`
- `{ "rating.average": -1 }`
- `{ "createdAt": -1 }`

### product_series collection
- `{ "name": 1 }` - unique
- `{ "status": 1 }`

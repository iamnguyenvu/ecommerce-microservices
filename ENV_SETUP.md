# 🔧 **Environment Configuration Guide**

## 📋 **Quick Setup**

```bash
# 1. Copy environment template
cp .env.example .env

# 2. Edit with your actual values
# Use any text editor to replace placeholder values

# 3. Never commit .env to git
# (Already configured in .gitignore)
```

## 🔐 **Security Checklist**

- [ ] `.env` file is in `.gitignore`
- [ ] No hardcoded secrets in source code
- [ ] JWT_SECRET is at least 32 characters
- [ ] Database passwords are strong
- [ ] Different credentials for each environment

## 🚀 **Environment Variables Explained**

### **Database Configuration**
```properties
POSTGRES_USER=authuser           # PostgreSQL username
POSTGRES_PASSWORD=SecurePass123  # PostgreSQL password
POSTGRES_DB=bookstore           # Database name
```

### **JWT Configuration**
```properties
JWT_SECRET=MyVerySecretKey32CharsLong  # JWT signing key
JWT_EXPIRATION=86400000                # Token expiration (24h)
```

### **Service Configuration**
```properties
AUTH_SERVICE_PORT=8081    # Auth service port
BOOK_SERVICE_PORT=8082    # Book service port
```

## 🔍 **Troubleshooting**

### **Issue: Service can't connect to database**
```bash
# Check if .env file exists
ls -la .env

# Check if variables are loaded
echo $AUTH_DB_USER
```

### **Issue: JWT token invalid**
```bash
# Make sure JWT_SECRET is at least 32 characters
echo $JWT_SECRET | wc -c
```

## 📁 **File Structure**
```
bookstore-microservices/
├── .env              # ← Your actual environment variables (Git ignored)
├── .env.example      # ← Template file (Git tracked)
├── .gitignore        # ← Git ignore rules
└── README.md         # ← This file
```

## 🛡️ **Production Notes**

- **Never commit `.env` to git**
- **Use Docker secrets in production**
- **Rotate secrets regularly**
- **Use different credentials per environment**
- **Monitor for exposed secrets**

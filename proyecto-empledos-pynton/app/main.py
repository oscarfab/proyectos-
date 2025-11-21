from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.database import engine, Base
from app.routers import auth_router, empleados_router
import time

# Crear las tablas en la base de datos
def create_tables():
    max_retries = 5
    retry_count = 0
    while retry_count < max_retries:
        try:
            Base.metadata.create_all(bind=engine)
            print("✅ Tablas creadas exitosamente")
            break
        except Exception as e:
            retry_count += 1
            print(f"❌ Intento {retry_count} fallido: {e}")
            if retry_count < max_retries:
                print(f"⏳ Reintentando en 5 segundos...")
                time.sleep(5)
            else:
                print("❌ No se pudieron crear las tablas después de varios intentos")
                raise

create_tables()

# Crear la aplicación FastAPI
app = FastAPI(
    title="API CRUD de Empleados",
    description="API REST para gestión de empleados con autenticación JWT",
    version="1.0.0"
)

# Configurar CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # En producción, especifica los orígenes permitidos
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Incluir los routers
app.include_router(auth_router.router)
app.include_router(empleados_router.router)

@app.get("/")
def root():
    return {
        "mensaje": "Bienvenido a la API de Empleados",
        "documentación": "/docs",
        "versión": "1.0.0"
    }

@app.get("/health")
def health_check():
    return {"status": "ok", "message": "API funcionando correctamente"}
from pydantic import BaseModel, EmailStr, Field
from typing import Optional
from datetime import datetime


# Schemas para Usuario
class UsuarioBase(BaseModel):
    email: EmailStr
    username: str


class UsuarioCreate(UsuarioBase):
    password: str = Field(..., min_length=6)


class Usuario(UsuarioBase):
    id: int
    is_active: bool
    created_at: datetime

    class Config:
        from_attributes = True


# Schemas para Token
class Token(BaseModel):
    access_token: str
    token_type: str


class TokenData(BaseModel):
    username: Optional[str] = None


# Schemas para Empleado
class EmpleadoBase(BaseModel):
    nombre: str = Field(..., min_length=1, max_length=100)
    apellido: str = Field(..., min_length=1, max_length=100)
    email: EmailStr
    puesto: str = Field(..., min_length=1, max_length=100)
    salario: float = Field(..., gt=0)
    departamento: Optional[str] = Field(None, max_length=100)
    telefono: Optional[str] = Field(None, max_length=20)


class EmpleadoCreate(EmpleadoBase):
    pass


class EmpleadoUpdate(BaseModel):
    nombre: Optional[str] = Field(None, min_length=1, max_length=100)
    apellido: Optional[str] = Field(None, min_length=1, max_length=100)
    email: Optional[EmailStr] = None
    puesto: Optional[str] = Field(None, min_length=1, max_length=100)
    salario: Optional[float] = Field(None, gt=0)
    departamento: Optional[str] = Field(None, max_length=100)
    telefono: Optional[str] = Field(None, max_length=20)


class Empleado(EmpleadoBase):
    id: int
    fecha_contratacion: datetime
    created_at: datetime
    updated_at: Optional[datetime] = None

    class Config:
        from_attributes = True
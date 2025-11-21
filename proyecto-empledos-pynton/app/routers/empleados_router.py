from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List
from app import models, schemas
from app.database import get_db
from app.auth import get_current_active_user

router = APIRouter(prefix="/empleados", tags=["Empleados"])


@router.post("/", response_model=schemas.Empleado, status_code=status.HTTP_201_CREATED)
def crear_empleado(
        empleado: schemas.EmpleadoCreate,
        db: Session = Depends(get_db),
        current_user: models.Usuario = Depends(get_current_active_user)
):
    """Crear un nuevo empleado (requiere autenticación)"""

    # Verificar si el email ya existe
    db_empleado = db.query(models.Empleado).filter(models.Empleado.email == empleado.email).first()
    if db_empleado:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="El email ya está registrado para otro empleado"
        )

    db_empleado = models.Empleado(**empleado.model_dump())
    db.add(db_empleado)
    db.commit()
    db.refresh(db_empleado)

    return db_empleado


@router.get("/", response_model=List[schemas.Empleado])
def listar_empleados(
        skip: int = 0,
        limit: int = 100,
        db: Session = Depends(get_db),
        current_user: models.Usuario = Depends(get_current_active_user)
):
    """Obtener lista de empleados (requiere autenticación)"""
    empleados = db.query(models.Empleado).offset(skip).limit(limit).all()
    return empleados


@router.get("/{empleado_id}", response_model=schemas.Empleado)
def obtener_empleado(
        empleado_id: int,
        db: Session = Depends(get_db),
        current_user: models.Usuario = Depends(get_current_active_user)
):
    """Obtener un empleado por ID (requiere autenticación)"""
    empleado = db.query(models.Empleado).filter(models.Empleado.id == empleado_id).first()
    if empleado is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Empleado no encontrado"
        )
    return empleado


@router.put("/{empleado_id}", response_model=schemas.Empleado)
def actualizar_empleado(
        empleado_id: int,
        empleado_update: schemas.EmpleadoUpdate,
        db: Session = Depends(get_db),
        current_user: models.Usuario = Depends(get_current_active_user)
):
    """Actualizar un empleado (requiere autenticación)"""
    db_empleado = db.query(models.Empleado).filter(models.Empleado.id == empleado_id).first()

    if db_empleado is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Empleado no encontrado"
        )

    # Verificar si el nuevo email ya existe en otro empleado
    if empleado_update.email:
        existing = db.query(models.Empleado).filter(
            models.Empleado.email == empleado_update.email,
            models.Empleado.id != empleado_id
        ).first()
        if existing:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="El email ya está registrado para otro empleado"
            )

    # Actualizar solo los campos que fueron enviados
    update_data = empleado_update.model_dump(exclude_unset=True)
    for key, value in update_data.items():
        setattr(db_empleado, key, value)

    db.commit()
    db.refresh(db_empleado)

    return db_empleado


@router.delete("/{empleado_id}", status_code=status.HTTP_204_NO_CONTENT)
def eliminar_empleado(
        empleado_id: int,
        db: Session = Depends(get_db),
        current_user: models.Usuario = Depends(get_current_active_user)
):
    """Eliminar un empleado (requiere autenticación)"""
    db_empleado = db.query(models.Empleado).filter(models.Empleado.id == empleado_id).first()

    if db_empleado is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Empleado no encontrado"
        )

    db.delete(db_empleado)
    db.commit()

    return None
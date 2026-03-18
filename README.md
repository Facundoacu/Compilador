🧠 Compilador - Proyecto Académico

Este proyecto consiste en el desarrollo de un compilador completo, implementando las distintas etapas clásicas del proceso de compilación. Fue realizado con fines educativos, abordando desde el análisis léxico hasta la generación de código intermedio y assembler.

---

## 📌 Características principales

El compilador desarrollado incluye:

- ✔️ Definición de la **gramática** del lenguaje
- ✔️ **Analizador Léxico**
- ✔️ **Analizador Sintáctico**
- ✔️ Generación de **Polaca Inversa** (código intermedio)
- ✔️ Generación de **código assembler simplificado**
- ✔️ Uso de herramientas tipo **YACC / BYACC / JavaCUP** (según implementación)
- ✔️ Manejo de estructuras de control (if, while, etc.)
- ✔️ Soporte para funciones y etiquetas

---

## ⚙️ Etapas del compilador

### 🔹 1. Análisis Léxico
Se encarga de:
- Leer el código fuente
- Identificar tokens (identificadores, palabras reservadas, operadores, etc.)
- Validar errores léxicos

---

### 🔹 2. Análisis Sintáctico
- Se basa en una gramática formal
- Verifica la estructura del programa
- Construye representaciones internas
- Implementado con herramienta tipo **YACC**

---

### 🔹 3. Generación de Polaca Inversa
- Traducción del código fuente a una forma intermedia
- Uso de estructuras tipo lista o pila
- Manejo de:
  - Expresiones
  - Saltos
  - Etiquetas
  - Control de flujo

---

### 🔹 4. Generación de Código Assembler
- Traducción de la polaca inversa a instrucciones assembler
- Versión simplificada (enfocada en lo académico)
- Manejo de variables, operaciones y saltos

---

## 🛠️ Tecnologías utilizadas

- Lenguaje: (Java / C / el que uses)
- Herramientas:
  - YACC / BYACC / CUP
  - (agregá otras si usaste)
- Estructuras:
  - Listas
  - Pilas
  - Tablas de símbolos

---

## 📁 Estructura del proyecto

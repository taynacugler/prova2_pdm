## Descrição

Este programa foi desenvolvido para a segunda prova da matéria de desenvolvimento para dispositivos móveis. 

### Funcionalidades

#### 1. Bicicletas
- **Cadastrar Bicicleta**:
  - **Código** (String)
  - **Modelo** (String)
  - **Material do Chassi** (String)
  - **Aro** (String)
  - **Preço** (Double)
  - **Quantidade de Marchas** (String)
  - **CPF do Cliente** (String - chave estrangeira)
- **Operações**:
  - Inserir
  - Listar todas
  - Buscar por nome/modelo
  - Buscar por código
  - Atualizar
  - Excluir

#### 2. Clientes
- **Cadastrar Cliente**:
  - **CPF** (String)
  - **Nome** (String)
  - **E-mail** (String)
  - **Instagram** (String)
- **Operações**:
  - Inserir
  - Listar todos
  - Buscar por nome
  - Buscar por CPF
  - Atualizar
  - Excluir

### Tecnologias Utilizadas

- **Linguagem**: Kotlin
- **Plataforma**: Android
- **Interface Gráfica**: Jetpack Compose
- **Banco de Dados**: Firebase Firestore

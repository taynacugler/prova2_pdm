package com.example.prova2cugler

sealed class Screens (val screen: String){
    data object Home: Screens("home")
    data object TelaCliente: Screens("Cliente")
    data object TelaBicicleta: Screens("Bicicletas")


}
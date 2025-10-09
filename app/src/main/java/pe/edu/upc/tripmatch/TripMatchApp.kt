package pe.edu.upc.tripmatch

import android.app.Application
import pe.edu.upc.tripmatch.presentation.di.PresentationModule

class TripMatchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PresentationModule.init(this)
    }
}

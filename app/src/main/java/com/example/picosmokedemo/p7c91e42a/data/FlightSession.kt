package com.example.picosmokedemo.p7c91e42a.data
import com.example.picosmokedemo.p7c91e42a.domain.FlightEngine
/** Shared integration point for the future device service and the spatial UI. */
object FlightSession {
 @Volatile var ready=false
 val training = com.example.picosmokedemo.p7c91e42a.domain.TrainingEngine()
 val coins = com.example.picosmokedemo.p7c91e42a.domain.CoinEngine()
 val engine = FlightEngine()
 val brainLink = BrainLinkAdapter(engine)
}

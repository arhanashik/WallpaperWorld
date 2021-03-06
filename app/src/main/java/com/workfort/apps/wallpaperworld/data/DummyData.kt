package com.workfort.apps.wallpaperworld.data

import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity

/*
*  ****************************************************************************
*  * Created by : Arhan Ashik on 1/2/2019 at 6:13 PM.
*  * Email : ashik.pstu.cse@gmail.com
*  * 
*  * Last edited by : Arhan Ashik on 1/2/2019.
*  * 
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>  
*  ****************************************************************************
*/

object DummyData {
    fun generateDummyData(): List<WallpaperEntity> {
        val listOfWallpapers = mutableListOf<WallpaperEntity>()

        var movieModel = WallpaperEntity(
            1,
            "Avengers",
            "",
            180,
            180,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            2,
            "Avengers: Age of Ultron",
            "",
            180,
            150,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            3,
            "Iron Man 3",
            "",
            180,
            130,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            4,
            "Avengers: Infinity War",
            "",
            190,
            190,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            5,
            "Thor: Ragnarok",
            "",
            180,
            205,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            6,
            "lack Panther",
            "",
            180,
            140,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            7,
            "Logan",
            "",
            180,
            150,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            8,
            "Logan",
            "",
            180,
            220,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            9,
            "Logan",
            "",
            180,
            140,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            10,
            "Logan",
            "",
            180,
            200,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            11,
            "Logan",
            "",
            180,
            210,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            12,
            "Logan",
            "",
            180,
            130,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            13,
            "Logan",
            "",
            180,
            220,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            14,
            "Logan",
            "",
            180,
            210,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            15,
            "Logan",
            "",
            180,
            185,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(
            16,
            "Logan",
            "",
            180,
            140,
            223,
            "",
            "arhan.ashik"
        )
        listOfWallpapers.add(movieModel)

        return listOfWallpapers
    }
}
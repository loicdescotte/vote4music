# Introduction
This project provides a complete sample application with Play and some additional frameworks (jQuery, lambdaJ).

This application allows to vote for music albums. The top 100 albums for each genre of music is calculated for each year. Example: the most popular rock album of 1991 was Nirvana's Nevermind.

vote4music sample app covers the following features:

* A REST API to view lists of albums (by genre, year ...) and create new ones
* CRUD (create read update delete) albums, with ajax sorting and pagination
* HTML5 client side validation based on model annotations
* Search filters
* Custom tags
* jQuery
* LamdaJ for sorting and filtering collections
* Authentication, roles and security
* Test classes
* ...

Bonus feature : WebSocket demo (asynchronous request, push request to refresh the browser)

# Play framework Tutorials
vote4Music is an application I've created to have a base of examples for my tutorials on Play Framework. You can read them to understand the code of the application.

You can check this tutorials [here](http://coffeebean.loicdescotte.com/search/label/planetplay)

If you're a french reader, you can check [play.rules](https://github.com/3monkeys/play.rules), an open source ebook about Play that integrates the tutorials.

# Application and API usage
See [wiki](https://github.com/loicdescotte/vote4music/wiki/Application-and-API-usage) for information about application and API usage

# Scala version

There is also a Play Scala version of this application.
You can check the Scala version [here](https://github.com/loicdescotte/vote4music-scala)
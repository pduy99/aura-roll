package com.helios.auraroll.quotes

/**
 * Pure-Kotlin in-memory quote repository. Quotes are grouped by the editorial color names
 * produced by `hueToColorName`. Every family rotates a small set so each visit feels fresh.
 */
class DefaultHueQuoteRepository : HueQuoteRepository {

    override fun quotesForHue(hueLabel: String): List<HueQuote> =
        quotesByHue[hueLabel] ?: fallbackQuotes

    private companion object {
        private val quotesByHue: Map<String, List<HueQuote>> = mapOf(
            // Reds
            "Volcanic Red" to listOf(
                HueQuote(
                    "Red protects itself. No color is as territorial. It stakes a claim, is alert and its throb calls for an answering pulse.",
                    "Derek Jarman"
                ),
                HueQuote(
                    "Red is the ultimate cure for sadness.",
                    "Bill Blass"
                ),
                HueQuote(
                    "If one says \u0027Red\u0027 and there are fifty people listening, it can be expected that there will be fifty reds in their minds.",
                    "Josef Albers"
                ),
                HueQuote(
                    "Red is such an interesting color to correlate with emotion, because it\u0027s on both ends of the spectrum.",
                    "Taylor Swift"
                ),
                HueQuote(
                    "Red is the first color of spring. It\u0027s the real color of rebirth. Of beginning.",
                    "Ally Condie"
                ),
                HueQuote(
                    "When in doubt, wear red.",
                    "Bill Blass"
                ),
                HueQuote(
                    "Red invades, attacks, conquers \u2014 it cannot be ignored.",
                    "Anonymous"
                ),
                HueQuote(
                    "I\u0027ve been forty years discovering that the queen of all colors is red.",
                    "Pierre-Auguste Renoir"
                ),
            ),
            "Rose Dusk" to listOf(
                HueQuote(
                    "Just when the caterpillar thought the world was over, it became a butterfly.",
                    "Proverb"
                ),
                HueQuote(
                    "Pink isn\u0027t just a color, it\u0027s an attitude.",
                    "Miley Cyrus"
                ),
                HueQuote(
                    "All the flowers of all the tomorrows are in the seeds of today.",
                    "Indian Proverb"
                ),
                HueQuote(
                    "Pink is the navy blue of India.",
                    "Diana Vreeland"
                ),
                HueQuote(
                    "I think pink is a beautiful color, and so flattering for many skin tones.",
                    "Cate Blanchett"
                ),
                HueQuote(
                    "There is a shade of red for every woman.",
                    "Audrey Hepburn"
                ),
                HueQuote(
                    "Pink is powerful. It softens the edges of strength.",
                    "Anonymous"
                ),
                HueQuote(
                    "The softest petals can break through the hardest soil.",
                    "Anonymous"
                ),
            ),

            // Orange
            "Desert Amber" to listOf(
                HueQuote(
                    "Orange is red brought nearer to humanity by yellow.",
                    "Johann Wolfgang von Goethe"
                ),
                HueQuote(
                    "Orange strengthens your emotional body, encouraging a general feeling of joy, well-being, and cheerfulness.",
                    "Tae Yun Kim"
                ),
                HueQuote(
                    "Nature always wears the colors of the spirit.",
                    "Ralph Waldo Emerson"
                ),
                HueQuote(
                    "Orange is the happiest color.",
                    "Frank Sinatra"
                ),
                HueQuote(
                    "If it weren\u0027t for the rocks in its bed, the stream would have no song.",
                    "Carl Perkins"
                ),
                HueQuote(
                    "The desert tells a different story every time one ventures on it.",
                    "Robert Edison Fulton Jr."
                ),
                HueQuote(
                    "Amber is the soul of the sun, captured in stone.",
                    "Anonymous"
                ),
                HueQuote(
                    "Light gives of itself freely, filling all available space.",
                    "Michael Strassfeld"
                ),
            ),

            // Yellow
            "Solar Gold" to listOf(
                HueQuote(
                    "How wonderful yellow is. It stands for the sun.",
                    "Vincent van Gogh"
                ),
                HueQuote(
                    "Yellow is capable of charming God.",
                    "Vincent van Gogh"
                ),
                HueQuote(
                    "Color is a power which directly influences the soul.",
                    "Wassily Kandinsky"
                ),
                HueQuote(
                    "Keep your face always toward the sunshine \u2014 and shadows will fall behind you.",
                    "Walt Whitman"
                ),
                HueQuote(
                    "Yellow wakes me up in the morning. Yellow gets me on the bike every day.",
                    "Lance Armstrong"
                ),
                HueQuote(
                    "The sun does not shine for a few trees and flowers, but for the wide world\u0027s joy.",
                    "Henry Ward Beecher"
                ),
                HueQuote(
                    "Gold is the child of Zeus, neither moth nor rust devoureth it.",
                    "Pindar"
                ),
                HueQuote(
                    "Sunshine is delicious, rain is refreshing, wind braces us up, snow is exhilarating.",
                    "John Ruskin"
                ),
            ),

            // Greens
            "Garden Lime" to listOf(
                HueQuote(
                    "Green is the prime color of the world, and that from which its loveliness arises.",
                    "Pedro Calderon de la Barca"
                ),
                HueQuote(
                    "The green of the leaves intensifies as it merges with the deep blue of the sky.",
                    "Claude Monet"
                ),
                HueQuote(
                    "Nature does not hurry, yet everything is accomplished.",
                    "Lao Tzu"
                ),
                HueQuote(
                    "Spring: a lovely reminder of how beautiful change can truly be.",
                    "Anonymous"
                ),
                HueQuote(
                    "And the day came when the risk to remain tight in a bud was more painful than the risk it took to blossom.",
                    "Anais Nin"
                ),
                HueQuote(
                    "A garden is a grand teacher. It teaches patience and careful watchfulness.",
                    "Gertrude Jekyll"
                ),
                HueQuote(
                    "Lime is the laughter of green.",
                    "Anonymous"
                ),
                HueQuote(
                    "Every leaf speaks bliss to me, fluttering from the autumn tree.",
                    "Emily Bronte"
                ),
            ),
            "Forest Emerald" to listOf(
                HueQuote(
                    "Absolute green is the most restful color, it does not move in any direction, has no overtones of joy or sorrow, of passion or of expectation.",
                    "Wassily Kandinsky"
                ),
                HueQuote(
                    "I am in love with green.",
                    "Vincent van Gogh"
                ),
                HueQuote(
                    "Adopt the pace of nature: her secret is patience.",
                    "Ralph Waldo Emerson"
                ),
                HueQuote(
                    "In every walk with nature, one receives far more than he seeks.",
                    "John Muir"
                ),
                HueQuote(
                    "The clearest way into the Universe is through a forest wilderness.",
                    "John Muir"
                ),
                HueQuote(
                    "Between every two pines is a doorway to a new world.",
                    "John Muir"
                ),
                HueQuote(
                    "Emerald is the gem of patient growth.",
                    "Anonymous"
                ),
                HueQuote(
                    "Trees are poems that the earth writes upon the sky.",
                    "Kahlil Gibran"
                ),
            ),
            "Jade Mist" to listOf(
                HueQuote(
                    "Green is a calm, soft color that nurtures us and helps us grow.",
                    "Olivia Wilde"
                ),
                HueQuote(
                    "Look deep into nature, and then you will understand everything better.",
                    "Albert Einstein"
                ),
                HueQuote(
                    "Each color lives by its mysterious life.",
                    "Wassily Kandinsky"
                ),
                HueQuote(
                    "Gold is for the mistress, silver for the maid; Copper for the craftsman cunning at his trade. \u0027Good!\u0027 said the Baron, sitting in his hall, \u0027But Iron \u2014 cold Iron \u2014 is master of them all.\u0027",
                    "Rudyard Kipling"
                ),
                HueQuote(
                    "Jade is the stone that whispers wisdom to those who listen.",
                    "Anonymous"
                ),
                HueQuote(
                    "Mist is the breath of the mountain.",
                    "Anonymous"
                ),
                HueQuote(
                    "There is a way that nature speaks, that land speaks. Most of the time we are simply not patient enough to listen.",
                    "Linda Hogan"
                ),
                HueQuote(
                    "Stillness is where creativity and solutions are found.",
                    "Eckhart Tolle"
                ),
            ),

            // Cyan / Teal
            "Arctic Teal" to listOf(
                HueQuote(
                    "Blue is the only color which maintains its own character in all its tones.",
                    "Raoul Dufy"
                ),
                HueQuote(
                    "The sea, once it casts its spell, holds one in its net of wonder forever.",
                    "Jacques Cousteau"
                ),
                HueQuote(
                    "Teal is the color of clarity \u2014 the meeting of sea and sky.",
                    "Anonymous"
                ),
                HueQuote(
                    "In the depth of winter, I finally learned that within me there lay an invincible summer.",
                    "Albert Camus"
                ),
                HueQuote(
                    "The cure for anything is salt water: sweat, tears or the sea.",
                    "Isak Dinesen"
                ),
                HueQuote(
                    "Ice contains no future, just the past, sealed away.",
                    "Haruki Murakami"
                ),
                HueQuote(
                    "Cold and silent, the arctic is the world\u0027s most patient teacher.",
                    "Anonymous"
                ),
                HueQuote(
                    "We need the tonic of wildness.",
                    "Henry David Thoreau"
                ),
            ),
            "Oceanic Cyan" to listOf(
                HueQuote(
                    "Blue is the only color which maintains its own character in all its tones.",
                    "Raoul Dufy"
                ),
                HueQuote(
                    "Blue color is everlastingly appointed by the deity to be a source of delight.",
                    "John Ruskin"
                ),
                HueQuote(
                    "The sea is everything. It covers seven tenths of the terrestrial globe.",
                    "Jules Verne"
                ),
                HueQuote(
                    "The voice of the sea speaks to the soul.",
                    "Kate Chopin"
                ),
                HueQuote(
                    "We are tied to the ocean. And when we go back to the sea, we are going back from whence we came.",
                    "John F. Kennedy"
                ),
                HueQuote(
                    "Live in the sunshine, swim in the sea, drink the wild air.",
                    "Ralph Waldo Emerson"
                ),
                HueQuote(
                    "The ocean stirs the heart, inspires the imagination and brings eternal joy to the soul.",
                    "Robert Wyland"
                ),
                HueQuote(
                    "Cyan is the song the sky sings to the sea.",
                    "Anonymous"
                ),
            ),

            // Blue / Indigo
            "Sapphire Blue" to listOf(
                HueQuote(
                    "Indigo is the color of the midnight sky when the moon is full, the color of the deep sea where the light starts to fade.",
                    "Anonymous"
                ),
                HueQuote(
                    "I never get tired of the blue sky.",
                    "Vincent van Gogh"
                ),
                HueQuote(
                    "Blue thou art, intensely blue; flower, whence came thy dazzling hue?",
                    "James Montgomery"
                ),
                HueQuote(
                    "Blue has no dimensions. It is beyond dimensions.",
                    "Yves Klein"
                ),
                HueQuote(
                    "Why do two colors, put one next to the other, sing? Can one really explain this?",
                    "Pablo Picasso"
                ),
                HueQuote(
                    "When I haven\u0027t any blue I use red.",
                    "Pablo Picasso"
                ),
                HueQuote(
                    "Blue is the typical heavenly color. The ultimate feeling it creates is one of rest.",
                    "Wassily Kandinsky"
                ),
                HueQuote(
                    "Sapphires whisper the secrets of the sky.",
                    "Anonymous"
                ),
            ),

            // Violet / Purple
            "Twilight Violet" to listOf(
                HueQuote(
                    "Violet is the color of the transition from the physical to the spiritual. It is the color of the soul.",
                    "Alice Walker"
                ),
                HueQuote(
                    "Purple is the most beautiful color and it\u0027s really hard to find a good purple.",
                    "Manolo Blahnik"
                ),
                HueQuote(
                    "I think it pisses God off if you walk by the color purple in a field somewhere and don\u0027t notice it.",
                    "Alice Walker"
                ),
                HueQuote(
                    "Twilight drops her curtain down, and pins it with a star.",
                    "Lucy Maud Montgomery"
                ),
                HueQuote(
                    "There\u0027s a sunrise and a sunset every single day, and they\u0027re absolutely free. Don\u0027t miss so many of them.",
                    "Jo Walton"
                ),
                HueQuote(
                    "Violet hours belong to dreamers.",
                    "Anonymous"
                ),
                HueQuote(
                    "How lovely the silence of growing things.",
                    "Anonymous"
                ),
                HueQuote(
                    "I shall wear purple, and learn to spit.",
                    "Jenny Joseph"
                ),
            ),
            "Amethyst Purple" to listOf(
                HueQuote(
                    "Purple does not show itself with vanity, but it impresses itself.",
                    "Anonymous"
                ),
                HueQuote(
                    "Violet is the color of dreams.",
                    "Anonymous"
                ),
                HueQuote(
                    "Color is the place where our brain and the universe meet.",
                    "Paul Klee"
                ),
                HueQuote(
                    "Purple is royalty. A queen\u0027s color.",
                    "Janelle Monae"
                ),
                HueQuote(
                    "Amethyst is the stone of stillness, of quiet conviction.",
                    "Anonymous"
                ),
                HueQuote(
                    "Color directly influences the soul.",
                    "Wassily Kandinsky"
                ),
                HueQuote(
                    "I love purple. Purple makes me happy.",
                    "Donna Karan"
                ),
                HueQuote(
                    "Mere color, unspoiled by meaning, can speak to the soul in a thousand different ways.",
                    "Oscar Wilde"
                ),
            ),
        )

        private val fallbackQuotes = listOf(
            HueQuote(
                "Color is a power which directly influences the soul.",
                "Wassily Kandinsky"
            ),
            HueQuote(
                "Mere color, unspoiled by meaning, can speak to the soul in a thousand different ways.",
                "Oscar Wilde"
            ),
            HueQuote(
                "Life is a great big canvas; throw all the paint on it you can.",
                "Danny Kaye"
            ),
            HueQuote(
                "The purest and most thoughtful minds are those which love color the most.",
                "John Ruskin"
            ),
        )
    }
}

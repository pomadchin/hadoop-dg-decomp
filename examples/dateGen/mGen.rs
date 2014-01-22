#!/usr/bin/Rscript

require(bigmemory)
require(bigalgebra)
require(irlba)

con <- file("mat.txt", open = "a")
replicate(1, {
    x <- matrix(rnorm(5 * 5), nrow = 5)
    write.table(x, file  = 'mat.txt', append = TRUE,
            row.names = FALSE, col.names = FALSE)
})

file.info("mat.txt")$size
close(con)
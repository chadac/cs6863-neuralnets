# Making the plots
# assumes that ./run-experiments.sh has been evaluated.

parse.results <- function(values, data.dir, fn.template, xlab) {
    n <- length(values)
    results <- data.frame(value=values, intensity=numeric(n), completed=numeric(n), time=numeric(n))
    fn.template <- paste(data.dir, fn.template, sep="")
    for(i in 1:n) {
        x <- values[i]
        data.sim <- read.csv(sprintf(fn.template, x, "sim"), sep=',')
        data.time <- read.csv(sprintf(fn.template, x, "time"), sep=",")
        results$intensity[i] <- mean(data.sim$total.intensity)
        results$time[i] <- mean(data.sim$total.time)
        results$completed[i] <- mean(data.sim$signal.complete == "true")
    }

    pdf(paste(data.dir, "sim-intensity.pdf", sep=""))
    plot(values, results$intensity, type="l", lwd=2, xlab=xlab, ylab="Intensity")
    dev.off()

    pdf(paste(data.dir, "sim-completed.pdf", sep=""))
    plot(values, 100*results$completed, type="l", lwd=2, xlab=xlab, ylab="% Completed")
    dev.off()

    pdf(paste(data.dir, "sim-time.pdf", sep=""))
    plot(values, results$time, type="l", lwd=2, xlab=xlab, ylab="Total time")
    dev.off()

    results
}

## Neurons-per-node

parse.results(seq(5, 100, by=5), "results/neurons/", "n%03d_%s.csv", "Neurons per node")

parse.results(seq(1, 10, by=0.2), "results/sparse/", "s%04.1f_%s.csv", "Sparsity")

parse.results(seq(1, 3, by=0.2), "results/sigmoid-sparse/", "s%04.1f_%s.csv", "Sparsity")

parse.results(seq(1, 10, by=0.2), "results/scale/", "s%04.1f_%s.csv", "Scale")

## sp.values <- seq(1, 10, by=0.1)
## sp.intensity <- numeric(length(sp.values))

## for(i in 1:length(sp.values)) {
##     sp <- sp.values[i]
##     data.sim <- read.csv(sprintf("results/sparse/s%04.1f_sim.csv", sp), sep=',')
##     data.time <- read.csv(sprintf("results/sparse/s%04.1f_time.csv", sp), sep=',')
##     sp.intensity[i] <- mean(data.sim$total.intensity)
## }

## ## pdf("results/sparse/sim.pdf")
## plot(sp.values, sp.intensity, type="l", lwd=2, xlab="Sparsity (mu)", ylab="Intensity")
## ## dev.off()

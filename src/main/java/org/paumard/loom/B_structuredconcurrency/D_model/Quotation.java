package org.paumard.loom.B_structuredconcurrency.D_model;

import jdk.incubator.concurrent.StructuredTaskScope;
import org.paumard.loom.B_structuredconcurrency.B_model.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public record Quotation(String agency, int amount) {

    private static Random random = new Random();

    public static Quotation readQuotation() {
        try (var scope = new QuotationScope()) {
            Future<Quotation> futureA = scope.fork(Quotation::readQuotationFromA);
            Future<Quotation> futureB = scope.fork(Quotation::readQuotationFromB);
            Future<Quotation> futureC = scope.fork(Quotation::readQuotationFromC);

            scope.join();

            return scope.bestQuotation();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Quotation readQuotationFromA() {
        try {
            Thread.sleep(random.nextInt(30, 90));
            return new Quotation("A", random.nextInt(40, 110));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Quotation readQuotationFromB() {
        try {
            Thread.sleep(random.nextInt(30, 90));
            return new Quotation("B", random.nextInt(40, 110));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Quotation readQuotationFromC() {
        try {
            Thread.sleep(random.nextInt(30, 90));
            return new Quotation("C", random.nextInt(40, 110));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

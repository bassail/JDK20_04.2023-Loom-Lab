package org.paumard.loom.B_structuredconcurrency.D_model;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Future;

public class QuotationScope extends StructuredTaskScope<Quotation> {

    private Quotation quotation;

    private final Collection<Quotation> quotations = new ConcurrentLinkedDeque<>();
    private final Collection<Throwable> exceptions = new ConcurrentLinkedDeque<>();

    public static class QuotationException extends RuntimeException{
        public QuotationException(String message) {
            super(message);
        }
    }

    @Override
    protected void handleComplete(Future<Quotation> future) {
        if (future.state() == Future.State.SUCCESS) {
            this.quotation = future.resultNow();
        }
        switch (future.state()){
            case RUNNING -> throw new IllegalStateException("OOps");
            case SUCCESS -> this.quotations.add(future.resultNow());
            case FAILED -> this.exceptions.add(future.exceptionNow());
            case CANCELLED -> {}
        }
    }

    public Quotation getResult(){
        return quotation;
    }

    public Quotation bestQuotation() {
        for (Quotation quotation :
                quotations) {
            System.out.println(quotation.amount());
        }
        return this.quotations.stream()
                .min(Comparator.comparingInt(Quotation::amount))
                .orElseThrow(() -> exceptions());
    }

    public QuotationException exceptions(){
        QuotationException exception = new QuotationException("Oopsie");
        for (Throwable throwable : this.exceptions) {
            exception.addSuppressed(throwable);
        }
        return exception;
    }

}
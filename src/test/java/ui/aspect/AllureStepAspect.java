package ui.aspect;

import io.qameta.allure.Allure;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AllureStepAspect {

    @Around("@annotation(io.qameta.allure.Step)")
    public Object measureStepExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();

        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            Allure.step("Step '" + methodName + "' выполнен за " + duration + " мс");
        }
    }
}

import { useEffect, useRef } from 'react';

/**
 * Hook that calls a function every interval.
 *
 * @param callback the function to call, if it returns true, the interval is cleared
 * @param delay the delay between calls
 * @param dependencies the dependencies of the hook
 */
export function useInterval(
  callback: () => Promise<boolean> | boolean | void,
  delay: number,
  dependencies: any[] = []
) {
    const savedCallback = useRef<() => Promise<boolean> | boolean | void>();

    useEffect(() => {
        savedCallback.current = callback;
    }, [callback]);

    useEffect(() => {
        async function tick() {
            if (savedCallback.current) {
                const shouldStop = await savedCallback.current();

                if (!shouldStop) {
                    setTimeout(tick, delay);
                }
            }
        }
        const id = setTimeout(tick, delay);
        return () => clearTimeout(id);
    }, [delay, dependencies]);
}


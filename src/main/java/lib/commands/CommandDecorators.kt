package lib.commands

import edu.wpi.first.wpilibj2.command.Command
import java.util.function.BooleanSupplier

infix fun Command.withTimeout(timeout: Double): Command = this.withTimeout(timeout)
infix fun Command.until(condition: BooleanSupplier): Command = this.until(condition)
infix fun Command.onlyWhile(condition: BooleanSupplier): Command = this.onlyWhile(condition)
infix fun Command.andThen(other: Command): Command = this.andThen(other)
infix fun Command.deadlineWith(other: Command): Command = this.deadlineWith(other)
infix fun Command.alongWith(other: Command): Command = this.alongWith(other)
infix fun Command.raceWith(other: Command): Command = this.raceWith(other)
infix fun Command.unless(condition: BooleanSupplier): Command = this.unless(condition)
infix fun Command.onlyIf(condition: BooleanSupplier): Command = this.onlyIf(condition)